package com.example.app.ui.main.home

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.app.R
import com.example.app.api.Category
import com.example.app.api.Product
import com.example.app.base.BaseFragment
import com.example.app.di.InjectingSavedStateViewModelFactory
import com.example.app.ui.main.MainFragment
import com.example.app.utils.views.ScrollStateHolder
import com.example.app.utils.views.enforceSingleScrollDirection
import com.github.ajalt.timberkt.d
import kotlinx.android.synthetic.main.home_fragment.*
import javax.inject.Inject

class HomeFragment : BaseFragment() {
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
        return inflater.inflate(R.layout.home2_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this, savedStateFactory.create(this))[HomeViewModel::class.java]
        scrollStateHolder = ScrollStateHolder(viewModel.recyclerStateBundle)
        setListeners()
        setRecycler()
    }

    fun setRecycler(){
        categoriesAdapter = CategoriesAdapter(scrollStates = viewModel.scrollStates, scrollStateHolder = scrollStateHolder)
        homeRecyclerView.enforceSingleScrollDirection()
        homeRecyclerView.adapter = categoriesAdapter
        categoriesAdapter.submitList(setFakeData())
    }

    fun setListeners(){
        fab.setOnClickListener {
            getMainFragment().goToLogin(0)
        }
    }


    override fun onPause() {
        super.onPause()
        viewModel.scrollStates = categoriesAdapter.scrollStates
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
                        name = "Produto 2"
                    ),
                    Product(
                        id = 3,
                        name = "Produto 3"
                    ),
                    Product(
                        id = 4,
                        name = "Produto 4"
                    ),
                    Product(
                        id = 5,
                        name = "Produto 5"
                    ),
                    Product(
                        id = 6,
                        name = "Produto 6"
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
                        name = "Produto 1"
                    ),
                    Product(
                        id = 2,
                        name = "Produto 2"
                    ),
                    Product(
                        id = 3,
                        name = "Produto 3"
                    ),
                    Product(
                        id = 4,
                        name = "Produto 4"
                    ),
                    Product(
                        id = 5,
                        name = "Produto 5"
                    ),
                    Product(
                        id = 6,
                        name = "Produto 6"
                    ),
                    Product(
                        id = 7,
                        name = "Produto 7"
                    ),
                    Product(
                        id = 8,
                        name = "Produto 8"
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
                        name = "Produto 1"
                    ),
                    Product(
                        id = 2,
                        name = "Produto 2"
                    ),
                    Product(
                        id = 3,
                        name = "Produto 3"
                    ),
                    Product(
                        id = 4,
                        name = "Produto 4"
                    ),
                    Product(
                        id = 5,
                        name = "Produto 5"
                    ),
                    Product(
                        id = 6,
                        name = "Produto 6"
                    ),
                    Product(
                        id = 7,
                        name = "Produto 7"
                    ),
                    Product(
                        id = 8,
                        name = "Produto 8"
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
                        name = "Produto 1"
                    ),
                    Product(
                        id = 2,
                        name = "Produto 2"
                    ),
                    Product(
                        id = 3,
                        name = "Produto 3"
                    ),
                    Product(
                        id = 4,
                        name = "Produto 4"
                    ),
                    Product(
                        id = 5,
                        name = "Produto 5"
                    ),
                    Product(
                        id = 6,
                        name = "Produto 6"
                    ),
                    Product(
                        id = 7,
                        name = "Produto 7"
                    ),
                    Product(
                        id = 8,
                        name = "Produto 8"
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
                        name = "Produto 1"
                    ),
                    Product(
                        id = 2,
                        name = "Produto 2"
                    ),
                    Product(
                        id = 3,
                        name = "Produto 3"
                    ),
                    Product(
                        id = 4,
                        name = "Produto 4"
                    ),
                    Product(
                        id = 5,
                        name = "Produto 5"
                    ),
                    Product(
                        id = 6,
                        name = "Produto 6"
                    ),
                    Product(
                        id = 7,
                        name = "Produto 7"
                    ),
                    Product(
                        id = 8,
                        name = "Produto 8"
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
        scrollStateHolder?.onSaveInstanceState(outState)
        viewModel.recyclerStateBundle = outState

    }

}