package com.hurrypizza.test

import android.content.Context
import android.graphics.Color
 import android.graphics.Paint
import android.graphics.Rect
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Button
import android.widget.GridView
import androidx.core.view.get
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hurrypizza.test.Gallery.Frag2_Adapter
import com.hurrypizza.test.Gallery.GalleryItem

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SecondFragmentSelect.newInstance] factory method to
 * create an instance of this fragment.
 */
class SecondFragmentSelect : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var viewOfLayout: View
    internal lateinit var gv: RecyclerView

    private lateinit var myContext: FragmentActivity
    private lateinit var fragManager: FragmentManager
    private lateinit var fragTransaction: FragmentTransaction

    private lateinit var newFolderFragment: SecondFragmentNewFolder
    private lateinit var setDirDestFragment: SecondFragmentSetDirDest

    var spanCount: Int = 2
    var initially_selected: Int? = null

    private var selectedIndices = arrayListOf<Int>()

    var items: ArrayList<GalleryItem> = ArrayList<GalleryItem>()

    lateinit var caller: SecondFragmentGallery

    override fun onAttach(context: Context) {
        super.onAttach(context)
        myContext = context as FragmentActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        viewOfLayout = inflater.inflate(R.layout.fragment_second_select, container, false)

        fragManager = myContext.supportFragmentManager

        val paint = Paint()
        paint.setColor(Color.BLACK)
        paint.alpha = 70

        gv = viewOfLayout.findViewById(R.id.selectGridView)

        var adapter = Frag2_Adapter(myContext, items)
        adapter.setOnItemClickListener { v, pos ->
            if (selectedIndices.contains(pos)){
                selectedIndices.remove(pos)
                if (v != null) {
                    v.alpha = 1.0F
                }
            } else {
                selectedIndices.add(pos)
                if (v != null) {
                    //v.setBackgroundColor(paint.color)
                    v.alpha = 0.4F
                }

            }
        }
        gv.setAdapter(adapter)

        val gm = GridLayoutManager(requireContext(), spanCount)
        gv.layoutManager = gm

        val spacing: Int = getResources().getDimensionPixelSize(R.dimen.recycler_spacing);
        gv.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(
                outRect: Rect,
                view: View,
                parent: RecyclerView,
                state: RecyclerView.State
            ) {
                outRect.left = spacing;
                outRect.right = spacing;
                outRect.bottom = spacing;
                outRect.top = spacing;
            }
        })
        gv.setHasFixedSize(true)

        gv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                when (newState) {
                    RecyclerView.SCROLL_STATE_DRAGGING -> {
                        if (initially_selected != null) {
                            Log.d("selectFragment", "init_select")
                            gv.layoutManager?.findViewByPosition(initially_selected!!)?.alpha = 0.4F
                            gv.removeOnScrollListener(this)
                        }
                    }
                    else -> {
                    }
                }
                super.onScrollStateChanged(recyclerView, newState)
            }
        })
//        selectedIndices.add(initially_selected)
////        var initially_selected_view = gv.adapter.On
/*
        gv.setOnItemClickListener(object: AdapterView.OnItemClickListener {
            override fun onItemClick(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (selectedIndices.contains(position)){
                    selectedIndices.remove(position)
                    if (view != null) {
                        view.alpha = 1.0F
                    }
                } else {
                    selectedIndices.add(position)
                    if (view != null) {
                        view.setBackgroundColor(paint.color)
                        view.alpha = 0.4F
                    }
                }

            }
        })
*/
        var mkdirButton = viewOfLayout.findViewById<Button>(R.id.mkdirButton)
        var copyButton = viewOfLayout.findViewById<Button>(R.id.copyButton)
        var moveButton = viewOfLayout.findViewById<Button>(R.id.moveButton)
        var deleteButton = viewOfLayout.findViewById<Button>(R.id.deleteButton)

        mkdirButton.setOnClickListener{
            if (selectedIndices.size != 0) {
                newFolderFragment = SecondFragmentNewFolder()
                newFolderFragment.items = items.slice(selectedIndices) as ArrayList<GalleryItem>
                newFolderFragment.caller = caller
                newFolderFragment.spanCount = spanCount

                fragTransaction = fragManager.beginTransaction()
                fragTransaction.replace(R.id.secondFragment, newFolderFragment)
                fragTransaction.commit()
            }
        }

        copyButton.setOnClickListener{
            if (selectedIndices.size != 0) {
                val newItems = items.slice(selectedIndices) as ArrayList<GalleryItem>
                for (item in newItems) {
                    if (item.type == 1) {
                        Toast.makeText(context, "폴더는 복사할 수 없습니다.", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }
                }
                setDirDestFragment = SecondFragmentSetDirDest()
                setDirDestFragment.items = newItems
                setDirDestFragment.caller = caller
                setDirDestFragment.copy_mode = true

                fragTransaction = fragManager.beginTransaction()
                fragTransaction.replace(R.id.secondFragment, setDirDestFragment)
                fragTransaction.commit()
            }
        }

        moveButton.setOnClickListener {
            if (selectedIndices.size != 0) {
                setDirDestFragment = SecondFragmentSetDirDest()
                setDirDestFragment.items = items.slice(selectedIndices) as ArrayList<GalleryItem>
                setDirDestFragment.caller = caller
                setDirDestFragment.copy_mode = false

                fragTransaction = fragManager.beginTransaction()
                fragTransaction.replace(R.id.secondFragment, setDirDestFragment)
                fragTransaction.commit()
            }
        }

        deleteButton.setOnClickListener {
            if (selectedIndices.size != 0) {
                for (i in selectedIndices) {
                    caller.items.removeAt(i)
                }
                fragManager.popBackStack()
            }
        }

        if (initially_selected != null) {
            selectedIndices.add(initially_selected!!)
            gv.layoutManager?.scrollToPosition(initially_selected!!)
        }

        return viewOfLayout
    }

    override fun onResume() {

        super.onResume()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SecondFragmentSelect.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SecondFragmentSelect().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}