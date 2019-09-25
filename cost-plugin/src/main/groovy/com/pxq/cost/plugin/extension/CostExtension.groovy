package com.pxq.cost.plugin.extension

import org.gradle.api.Project

/**
 * 接收额外的输入，如是否需要注入代码
 * author : pxq
 * date : 19-9-25 下午10:24
 */
class CostExtension{

    static final String EXTENSION_NAME = 'cost'

    //默认注入耗时计算
    boolean injectCost = true


    /**
     * 创建extension
     * @param project
     */
    static void create(Project project){
        project.extensions.create(CostExtension.EXTENSION_NAME, CostExtension)
    }

    /**
     * 判断是否需要注入
     * @param project
     * @return
     */
    static boolean checkInject(Project project){
        return project.extensions.getByName(CostExtension.EXTENSION_NAME).injectCost
    }

}