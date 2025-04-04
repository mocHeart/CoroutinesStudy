package com.hg.crs.demo07.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.hg.crs.databinding.FragmentD07ArticleBinding
import com.hg.crs.demo07.adapter.ArticleAdapter
import com.hg.crs.demo07.viewmodel.ArticleViewModel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class D07ArticleFragment : Fragment() {

    private val viewModel by viewModels<ArticleViewModel>()

    private val mBinding: FragmentD07ArticleBinding by lazy {
        FragmentD07ArticleBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return mBinding.root
    }

    // 获取关键字
    private fun TextView.textWatcherFlow(): Flow<String> = callbackFlow {
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(s: Editable?) {
                trySend(s.toString()).isSuccess
            }
        }
        addTextChangedListener(textWatcher)
        awaitClose { removeTextChangedListener(textWatcher) }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        lifecycleScope.launchWhenCreated {
            mBinding.dm07EtArticleSearch.textWatcherFlow().collect {
                Log.d("JY", "collect keywords: $it")
                viewModel.searchArticles(it)
            }
        }

        context?.let {
            val adapter = ArticleAdapter(it)
            mBinding.dm07ArticleRecyclerView.adapter = adapter
            viewModel.articles.observe(viewLifecycleOwner) { articles ->
                adapter.setData(articles)
            }
        }
    }
}