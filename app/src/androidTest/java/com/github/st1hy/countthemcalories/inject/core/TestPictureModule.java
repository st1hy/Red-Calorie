package com.github.st1hy.countthemcalories.inject.core;

import com.github.st1hy.countthemcalories.inject.core.headerpicture.PictureOthersModule;

import dagger.Module;

@Module(includes = {
        PictureOthersModule.class,
        TestPicturePickerModule.class
})
public class TestPictureModule {
}
