package com.github.st1hy.countthemcalories.ui.core.headerpicture

import android.app.Activity
import android.support.annotation.CheckResult
import android.widget.ImageView
import com.jakewharton.rxbinding.view.RxView
import kotlinx.android.synthetic.main.image_header_appbar.*
import rx.Observable
import javax.inject.Inject

internal interface PictureView {
    @CheckResult fun getSelectPictureObservable(): Observable<Void>
    val imageView: ImageView
}

internal class PictureViewImpl @Inject constructor(activity: Activity) : PictureView {

    override val imageView: ImageView = activity.image_header_image_view

    @CheckResult
    override fun getSelectPictureObservable() = RxView.clicks(imageView)
}
