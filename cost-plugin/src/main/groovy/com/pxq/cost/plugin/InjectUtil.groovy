package com.pxq.cost.plugin

import com.pxq.cost.api.MethodCost
import javassist.ClassPool
import javassist.CtClass
import javassist.CtMethod
import javassist.CtNewMethod
import org.gradle.api.Project

class InjectUtil {

    private static final String COST_SUFFIX = '$$Impl'

    static final ClassPool sClassPool = ClassPool.getDefault()


    static void injectCost(File baseClassPath, Project project) {
        println "injectUtil ${baseClassPath.path}"
        //把类路径添加到classpool
        sClassPool.appendClassPath(baseClassPath.path)
        //添加Android相关的类
        sClassPool.appendClassPath(project.android.bootClasspath[0].toString())
        if (baseClassPath.isDirectory()) {
            //遍历文件获取类
            baseClassPath.eachFileRecurse { classFile ->
                //过滤掉一些生成的类
                if (check(classFile)) {
                    println "find class : ${classFile.path}"

                    //把类文件路径转成类名
                    def className = convertClass(baseClassPath.path, classFile.path)
                    println className
                    //注入代码
                    inject(baseClassPath.path, className)
                }
            }
        }

    }

    /**
     * 向目标类注入耗时计算代码,生成同名的代理方法，在代理方法中调用原方法计算耗时
     * @param baseClassPath 写回原路径
     * @param clazz
     */
    private static void inject(String baseClassPath, String clazz) {
        def ctClass = sClassPool.get(clazz)
        //解冻
        if (ctClass.isFrozen()) {
            ctClass.defrost()
        }
        ctClass.getDeclaredMethods().each { ctMethod ->
            //判断是否要处理
            if (ctMethod.hasAnnotation(MethodCost.class)) {
                println "before ${ctMethod.name}"
                //把原方法改名，生成一个同名的代理方法，添加耗时计算
                def name = ctMethod.name
                def newName = name + COST_SUFFIX
                println "after ${newName}"
                def body = generateBody(ctClass, ctMethod, newName)
                println "generateBody : ${body}"
                //原方法改名
                ctMethod.setName(newName)
                //生成代理方法
                def proxyMethod = CtNewMethod.make(ctMethod.modifiers, ctMethod.returnType, name, ctMethod.parameterTypes, ctMethod.exceptionTypes, body, ctClass)
                //把代理方法添加进来
                ctClass.addMethod(proxyMethod)
            }
        }
        ctClass.writeFile(baseClassPath)
        ctClass.detach()//释放
    }

    /**
     * 生成代理方法体，包含原方法的调用和耗时打印
     * @param ctClass
     * @param ctMethod
     * @param newName
     * @return
     */
    private static String generateBody(CtClass ctClass, CtMethod ctMethod, String newName){
        //方法返回类型
        def returnType = ctMethod.returnType.name
        println returnType
        //生产的方法返回值
        def methodResult = "${newName}(\$\$);"
        if (!"void".equals(returnType)){
            //处理返回值
            methodResult = "${returnType} result = "+ methodResult
        }
        println methodResult
        return "{long costStartTime = System.currentTimeMillis();" +
                //调用原方法 xxx$$Impl() $$表示方法接收的所有参数
                methodResult +
                "android.util.Log.e(\"METHOD_COST\", \"${ctClass.name}.${ctMethod.name}() 耗时：\" + (System.currentTimeMillis() - costStartTime) + \"ms\");" +
                //处理一下返回值 void 类型不处理
                ("void".equals(returnType) ? "}" : "return result;}")

    }

    private static String convertClass(String baseClassPath, String classPath) {
        //截取包之后的路径
        def packagePath = classPath.substring(baseClassPath.length() + 1)
        //把 / 替换成.
        def clazz = packagePath.replaceAll("/", ".")
        //去掉.class 扩展名

        return clazz.substring(0, packagePath.length() - ".class".length())
    }


    //过滤掉一些生成的类
    private static boolean check(File file) {
        if (file.isDirectory()) {
            return false
        }

        def filePath = file.path

        return !filePath.contains('R$') &&
                !filePath.contains('R.class') &&
                !filePath.contains('BuildConfig.class')
    }

}