package com.test.krishna.ui.main

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.widget.Toast
import com.test.krishna.R
import com.test.krishna.cache.Cache
import com.test.krishna.models.Model
import com.test.krishna.ui.detailview.DetailViewActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), DeliveriesAdapter.OnItemClickListener {

    private val LIMIT: Int = 20

    private var disposable: Disposable? = null
    private lateinit var adapter: DeliveriesAdapter
    private val service by lazy {
        com.test.krishna.network.Service.create()
    }

    private lateinit var cache: Cache

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        getDeliveries(0)

        cache = Cache(this)

        recycler_view.layoutManager = LinearLayoutManager(this)
        adapter = DeliveriesAdapter(this, cache.cachedData)
        recycler_view.adapter = adapter
        adapter.setOnItemClickListener(this)

    }

    private fun setItems(deliveries: List<Model.Delivery>) {
        adapter.setItems(deliveries)
        cacheData(deliveries)
    }

    private fun cacheData(deliveries: List<Model.Delivery>) {
        cache.cachedData = deliveries
    }

    private fun getDeliveries(offset: Int) {
        disposable = service.getDeliveries(offset = offset, limit = LIMIT)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { result -> setItems(result) },
                        { error -> Toast.makeText(this, error.message, Toast.LENGTH_SHORT).show() }
                )
    }

    override fun onPause() {
        super.onPause()
        disposable?.dispose()
    }

    override fun onItemClick(position: Int, delivery: Model.Delivery) {
        DetailViewActivity.open(this, delivery)
    }

}