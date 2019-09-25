package com.pxq.cost.plugin

import com.android.build.api.transform.Format
import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.Transform
import com.android.build.api.transform.TransformException
import com.android.build.api.transform.TransformInvocation
import com.android.build.gradle.internal.pipeline.TransformManager
import com.pxq.cost.plugin.extension.CostExtension
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.FileUtils
import org.gradle.api.Project

/**
 * 编译过程中处理class文件
 * author : pxq
 * date : 19-9-22 下午4:11
 */
class ClassTransform extends Transform {

    Project mProject

    boolean inject = false

    ClassTransform(Project project) {
        mProject = project

    }

    @Override
    String getName() {
        return ClassTransform.simpleName
    }

    //输入类型，这里只处理class文件
    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS
    }

    @Override
    Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    @Override
    boolean isIncremental() {
        return false
    }

    @Override
    void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        super.transform(transformInvocation)
        println '---- transform start ----'
        inject = CostExtension.checkInject(mProject)
        println "injectCost = ${inject}"
        transformInvocation.inputs.each { input ->
            input.directoryInputs.each { dirInput ->
                if (inject) {
                    //注入cost统计代码
                    InjectUtil.injectCost(dirInput.file, mProject)
                }
                // 将input的目录复制到output指定目录 否则运行时会报ClassNotFound异常
                def dest = transformInvocation.outputProvider.getContentLocation(dirInput.name,
                        dirInput.contentTypes, dirInput.scopes, Format.DIRECTORY)
                FileUtils.copyDirectory(dirInput.file, dest)
            }
            //不处理jar文件
            input.jarInputs.each { jarInput ->
                // 重命名输出文件（同目录copyFile会冲突）
                def jarName = jarInput.name
                def md5Name = DigestUtils.md5Hex(jarInput.file.getAbsolutePath())
                if (jarName.endsWith(".jar")) {
                    jarName = jarName.substring(0, jarName.length() - 4)
                }
                def dest = transformInvocation.outputProvider.getContentLocation(jarName + md5Name, jarInput.contentTypes, jarInput.scopes, Format.JAR)
                FileUtils.copyFile(jarInput.file, dest)
            }
        }
        println '---- transform end ----'
    }
}