package com.photoapp.ui

import PhotosAdapter
import android.content.Context
import android.nfc.tech.MifareUltralight.PAGE_SIZE
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView.OnEditorActionListener
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.photoapp.R
import com.photoapp.data.model.Photo
import com.photoapp.data.model.PhotosModel
import com.photoapp.data.remote.ApiClient
import com.photoapp.data.remote.ApiInterface
import com.photoapp.utils.NetworkUtil
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    private lateinit var compositeDisposable: CompositeDisposable
    private var apiInterface: ApiInterface? = null
    private var arrayList = ArrayList<Photo>()
    private var pageNumber: Int = 1
    private var pageLimit: String = "1"
    private var isLastPage: Boolean = false
    private var isLoading: Boolean = false
    private lateinit var photosAdapter:PhotosAdapter
    private lateinit var layoutManager: StaggeredGridLayoutManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        init()

        et_search.setOnEditorActionListener(OnEditorActionListener { view, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                if (NetworkUtil.isNetworkAvailable(this@MainActivity)) {
                    resetSearch()
                    Toast.makeText(this@MainActivity,"Getting photos from flickr",Toast.LENGTH_SHORT).show()
                    fetchPhotosList(pageNumber.toString())
                    et_search.clearFocus()
                    val imm: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(et_search.windowToken, 0)
                } else {
                    Toast.makeText(this@MainActivity,"Connect to internet and try again later",Toast.LENGTH_SHORT).show()
                }
                return@OnEditorActionListener true
            }
            false
        })

        // Show clear button when search text appear
        et_search.doOnTextChanged { text, start, before, count ->
            if(count > 0){
                iv_clear.visibility = View.VISIBLE
            }else{
                iv_clear.visibility = View.GONE
            }
        }

        // clear search text
        iv_clear.setOnClickListener{
            et_search.setText("")
        }
    }

    private fun resetSearch() {
        photosAdapter.clear()
        pageNumber = 1
        isLastPage = false
    }

    private fun init() {
        compositeDisposable = CompositeDisposable()
        apiInterface = ApiClient.client!!.create(ApiInterface::class.java)
        et_search.requestFocus()

        // Recyclerview initialization
        layoutManager = StaggeredGridLayoutManager(3,StaggeredGridLayoutManager.VERTICAL)
        rv_photos.layoutManager = layoutManager

        photosAdapter = PhotosAdapter(this@MainActivity, arrayList)

        // Pagination
        rv_photos.addOnScrollListener(recyclerViewOnScrollListener)
        rv_photos.adapter = photosAdapter
    }

    /**
     * @param pageNo Page number for fetching photos
     */
    private fun fetchPhotosList(pageNo: String) {
        isLoading = false
        // Search text
        val searchText = et_search.text.toString()
        // Query parameter
        val queryMap = HashMap<String, String>()
        queryMap["api_key"] = resources.getString(R.string.flickr_api_key)
        queryMap["tags"] = searchText
        queryMap["per_page"] = "100"
        queryMap["page"] = pageNo
        queryMap["format"] = "json"
        queryMap["nojsoncallback"] = "1"

        compositeDisposable.add(
            apiInterface!!
                .getPhotosByTag(queryMap)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<PhotosModel>() {
                    override fun onSuccess(t: PhotosModel) {
                        try {
                            val status = t.stat!!
                            pageLimit = t.photos!!.pages!!
                            if (status == "ok") {
                                arrayList = t.photos!!.photo!!
                                if (arrayList.size > 0) {
                                    photosAdapter.addItems(arrayList)
                                }else{
                                    Toast.makeText(this@MainActivity,"No more photos to load",Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                Toast.makeText(this@MainActivity,"Unable to fetch photos. Try again later",Toast.LENGTH_SHORT).show()
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                            Toast.makeText(this@MainActivity,"Unable to fetch photos. Try again later",Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onError(e: Throwable) {
                        e.printStackTrace()
                        Toast.makeText(
                            this@MainActivity,"Unable to fetch photos. Try again later",Toast.LENGTH_SHORT).show()
                    }
                })
        )
    }

    private fun loadMoreItems() {
        isLoading = true
        pageNumber += 1
        fetchPhotosList(pageNumber.toString())
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }

    private val recyclerViewOnScrollListener: RecyclerView.OnScrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            val visibleItemCount: Int = layoutManager.childCount
            val totalItemCount: Int = layoutManager.itemCount
            val firstVisibleItemPosition: Int = layoutManager.findFirstVisibleItemPositions(null)[0]
            if (!isLoading && !isLastPage) {
                if (visibleItemCount + firstVisibleItemPosition >= totalItemCount && firstVisibleItemPosition >= 0 && totalItemCount >= PAGE_SIZE) {
                    loadMoreItems()
                }
            }
        }
    }
}