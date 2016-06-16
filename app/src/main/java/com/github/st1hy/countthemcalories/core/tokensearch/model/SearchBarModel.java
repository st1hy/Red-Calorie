package com.github.st1hy.countthemcalories.core.tokensearch.model;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class SearchBarModel {

    boolean isExpanded = false;
    final ParcelableProxy parcelableProxy;

    public SearchBarModel(@Nullable Bundle savedState) {
        ParcelableProxy parcelableProxy = null;
        if (savedState != null) {
            parcelableProxy = savedState.getParcelable(ParcelableProxy.SAVE_STATE);
            if (parcelableProxy != null) {
                isExpanded = parcelableProxy.isExpanded;
            }
        }
        if (parcelableProxy == null) parcelableProxy = new ParcelableProxy();
        this.parcelableProxy = parcelableProxy;
    }

    public boolean isExpanded() {
        return isExpanded;
    }

    public void setExpanded(boolean expanded) {
        isExpanded = expanded;
    }

    public void onSaveState(@NonNull Bundle bundle) {
        bundle.putParcelable(ParcelableProxy.SAVE_STATE, parcelableProxy.snapshot(this));
    }


    static class ParcelableProxy implements Parcelable {
        private static final String SAVE_STATE = "Search bar model state";
        private boolean isExpanded;

        @NonNull
        public ParcelableProxy snapshot(SearchBarModel model) {
            this.isExpanded = model.isExpanded;
            return this;
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            out.writeInt(isExpanded ? 1 : 0);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Parcelable.Creator<ParcelableProxy> CREATOR = new Parcelable.Creator<ParcelableProxy>() {
            public ParcelableProxy createFromParcel(Parcel in) {
                ParcelableProxy proxy = new ParcelableProxy();
                proxy.isExpanded = in.readInt() > 0;
                return proxy;
            }

            public ParcelableProxy[] newArray(int size) {
                return new ParcelableProxy[size];
            }
        };
    }
}
