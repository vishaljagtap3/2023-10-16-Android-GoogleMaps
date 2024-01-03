package com.bitcodetech.googlemaps

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.OnStreetViewPanoramaReadyCallback
import com.google.android.gms.maps.StreetViewPanorama
import com.google.android.gms.maps.SupportStreetViewPanoramaFragment
import com.google.android.gms.maps.model.LatLng

class SVPActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.avp_activity)

        val svpFragment = SupportStreetViewPanoramaFragment()
        supportFragmentManager.beginTransaction()
            .add(R.id.mainContainer, svpFragment, null)
            .commit()

        svpFragment.getStreetViewPanoramaAsync(
            object : OnStreetViewPanoramaReadyCallback {
                override fun onStreetViewPanoramaReady(svp: StreetViewPanorama) {
                    svp.isZoomGesturesEnabled = true
                    svp.isStreetNamesEnabled = true
                    svp.isPanningGesturesEnabled = true
                    svp.isUserNavigationEnabled = true
                    svp.setPosition(LatLng(18.9222602,72.8346319))
                }
            }
        )
    }

}