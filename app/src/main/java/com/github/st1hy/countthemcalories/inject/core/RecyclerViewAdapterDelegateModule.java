package com.github.st1hy.countthemcalories.inject.core;

import com.github.st1hy.countthemcalories.core.adapter.delegate.RecyclerAdapterWrapper;
import com.github.st1hy.countthemcalories.core.adapter.delegate.RecyclerViewAdapterDelegate;
import com.github.st1hy.countthemcalories.inject.PerFragment;

import dagger.Module;
import dagger.Provides;

@Module
public class RecyclerViewAdapterDelegateModule {

    @Provides
    @PerFragment
    public RecyclerViewAdapterDelegate recyclerViewAdapterDelegate(RecyclerAdapterWrapper wrapper) {
        return RecyclerViewAdapterDelegate.newAdapter(wrapper);
    }
}