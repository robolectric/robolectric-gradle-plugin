package org.robolectric.gradle

import com.android.build.gradle.AppPlugin
import org.gradle.api.Project
import org.gradle.internal.reflect.Instantiator
import org.gradle.tooling.provider.model.ToolingModelBuilderRegistry

import javax.inject.Inject

class ExtendedAppPlugin extends AppPlugin {

    @Inject
    ExtendedAppPlugin(Instantiator instantiator, ToolingModelBuilderRegistry registry) {
        super(instantiator, registry)
    }

    @Override
    void apply(Project project) {
        super.apply(project)
        project.extensions.create("extended-android", ExtendedAndroid)
    }
}

