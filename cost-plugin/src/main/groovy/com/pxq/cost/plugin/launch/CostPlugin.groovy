package com.pxq.cost.plugin.launch

import com.android.build.gradle.AppExtension
import com.pxq.cost.plugin.ClassTransform
import com.pxq.cost.plugin.extension.CostExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * 方法耗时插件，用来注册Transform
 * author : pxq
 * date : 19-9-22 下午3:43
 */
class CostPlugin implements Plugin<Project>{

    @Override
    void apply(Project project) {
        //AppExtension即android{...}
        def android = project.extensions.getByType(AppExtension)
        //创建cost extension
        CostExtension.create(project)
        //注册transform
        android.registerTransform(new ClassTransform(project))

    }
}
