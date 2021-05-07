package com.example.app.ui.main.home

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.example.app.R
import com.example.app.api.*
import com.example.app.base.BaseFragment
import com.example.app.di.InjectingSavedStateViewModelFactory
import com.example.app.ui.main.MainFragment
import com.example.app.utils.extensions.snack
import com.example.app.utils.views.ScrollStateHolder
import com.example.app.utils.views.enforceSingleScrollDirection
import com.github.ajalt.timberkt.d
import kotlinx.android.synthetic.main.collapsing_appbar_home.*
import kotlinx.android.synthetic.main.error_state.*
import kotlinx.android.synthetic.main.home_fragment.*
import kotlinx.android.synthetic.main.home_fragment.emptyLayout
import kotlinx.android.synthetic.main.home_fragment.errorLayout
import kotlinx.android.synthetic.main.home_fragment.loadingLayout
import javax.inject.Inject

class HomeFragment : BaseFragment(), ProductsAdapter.Interaction {

    @Inject
    lateinit var savedStateFactory: InjectingSavedStateViewModelFactory
    lateinit var categoriesAdapter: CategoriesAdapter

    companion object {
        fun newInstance() = HomeFragment()
    }

    private lateinit var viewModel: HomeViewModel

    var scrollStateHolder:ScrollStateHolder?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.home_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this, savedStateFactory.create(this))[HomeViewModel::class.java]
        scrollStateHolder = ScrollStateHolder(viewModel.recyclerStateBundle)
        setListeners()
        setObservers()
        setRecycler()
        getHome()
    }


    fun getHome(){
        viewModel.getHome()
    }

    fun setRecycler(){
        categoriesAdapter = CategoriesAdapter(scrollStates = viewModel.scrollStates, scrollStateHolder = scrollStateHolder, interaction = this)
        homeRecyclerView.enforceSingleScrollDirection()
        homeRecyclerView.adapter = categoriesAdapter
    }

    fun setListeners(){
        fab.setOnClickListener {
            getMainFragment().goToCast()
        }

        homeLogo.setOnClickListener {
            homeLogo.snack("teste", R.color.colorSnackError, {})
        }

        tryAgainBtn.setOnClickListener {
            getHome()
        }
    }

    fun setData(data: Home?){
        categoriesAdapter.submitList(data?.productsByCategory)

    }

    fun setObservers(){
        viewModel.observeHome().observe(viewLifecycleOwner, Observer {
            if (it.status == Resource.Status.SUCCESS) {
                loadingLayout.visibility = View.GONE
                errorLayout.visibility = View.GONE
                setData(it.data)

            } else if (it.status == Resource.Status.LOADING) {
                loadingLayout.visibility = View.VISIBLE
                errorLayout.visibility = View.GONE
                emptyLayout.visibility = View.GONE

            } else if (it.status == Resource.Status.ERROR) {
                loadingLayout.visibility = View.GONE
                errorLayout.visibility = View.VISIBLE
                emptyLayout.visibility = View.GONE
            }
        })

        sessionManager.getAuthUser().observe(viewLifecycleOwner, Observer {
            d{"user observe " + it.data.toString()}
//            getHome()
        })
    }


    override fun onPause() {
        super.onPause()
//        viewModel.scrollStates = categoriesAdapter.scrollStates
        scrollStateHolder?.onSaveInstanceState(viewModel.recyclerStateBundle)


    }

    fun setFakeData() : ArrayList<Category>{
        return arrayListOf(
            Category(
                id = 1,
                title = "Categoria 1",
                subtitle = "teste",
                items = arrayListOf(
                    Product(
                        id = 1,
                        product_name = "Produto 2"
                    ),
                    Product(
                        id = 3,
                        product_name = "Produto 3"
                    ),
                    Product(
                        id = 4,
                        product_name = "Produto 4"
                    ),
                    Product(
                        id = 5,
                        product_name = "Produto 5"
                    ),
                    Product(
                        id = 6,
                        product_name = "Produto 6"
                    ),
                )
            ),
            Category(
                id = 2,
                title = "Categoria 2",
                subtitle = "teste",
                items = arrayListOf(
                    Product(
                        id = 1,
                        product_name = "Produto 1"
                    ),
                    Product(
                        id = 2,
                        product_name = "Produto 2"
                    ),
                    Product(
                        id = 3,
                        product_name = "Produto 3"
                    ),
                    Product(
                        id = 4,
                        product_name = "Produto 4"
                    ),
                    Product(
                        id = 5,
                        product_name = "Produto 5"
                    ),
                    Product(
                        id = 6,
                        product_name = "Produto 6"
                    ),
                    Product(
                        id = 7,
                        product_name = "Produto 7"
                    ),
                    Product(
                        id = 8,
                        product_name = "Produto 8"
                    ),
                )
            ),
            Category(
                id = 3,
                title = "Categoria 3",
                subtitle = "teste",
                items = arrayListOf(
                    Product(
                        id = 1,
                        product_name = "Produto 1"
                    ),
                    Product(
                        id = 2,
                        product_name = "Produto 2"
                    ),
                    Product(
                        id = 3,
                        product_name = "Produto 3"
                    ),
                    Product(
                        id = 4,
                        product_name = "Produto 4"
                    ),
                    Product(
                        id = 5,
                        product_name = "Produto 5"
                    ),
                    Product(
                        id = 6,
                        product_name = "Produto 6"
                    ),
                    Product(
                        id = 7,
                        product_name = "Produto 7"
                    ),
                    Product(
                        id = 8,
                        product_name = "Produto 8"
                    ),
                )
            ),
            Category(
                id = 4,
                title = "Categoria 4",
                subtitle = "teste",
                items = arrayListOf(
                    Product(
                        id = 1,
                        product_name = "Produto 1"
                    ),
                    Product(
                        id = 2,
                        product_name = "Produto 2"
                    ),
                    Product(
                        id = 3,
                        product_name = "Produto 3"
                    ),
                    Product(
                        id = 4,
                        product_name = "Produto 4"
                    ),
                    Product(
                        id = 5,
                        product_name = "Produto 5"
                    ),
                    Product(
                        id = 6,
                        product_name = "Produto 6"
                    ),
                    Product(
                        id = 7,
                        product_name = "Produto 7"
                    ),
                    Product(
                        id = 8,
                        product_name = "Produto 8"
                    ),
                )
            ),
            Category(
                id = 5,
                title = "Categoria 5",
                subtitle = "teste",
                items = arrayListOf(
                    Product(
                        id = 1,
                        product_name = "Produto 1"
                    ),
                    Product(
                        id = 2,
                        product_name = "Produto 2"
                    ),
                    Product(
                        id = 3,
                        product_name = "Produto 3"
                    ),
                    Product(
                        id = 4,
                        product_name = "Produto 4"
                    ),
                    Product(
                        id = 5,
                        product_name = "Produto 5"
                    ),
                    Product(
                        id = 6,
                        product_name = "Produto 6"
                    ),
                    Product(
                        id = 7,
                        product_name = "Produto 7"
                    ),
                    Product(
                        id = 8,
                        product_name = "Produto 8"
                    ),
                )
            ),
        )
    }

    private fun getMainFragment(): MainFragment {
        return parentFragment?.parentFragment as MainFragment
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        d{"save instance"}
//        scrollStateHolder?.onSaveInstanceState(outState)
//        viewModel.recyclerStateBundle = outState

    }

    override fun onItemSelected(position: Int, item: Product) {
        safeNavigate(navController, HomeFragmentDirections.actionHomeFragmentToProductDetailFragment(item))
    }

}