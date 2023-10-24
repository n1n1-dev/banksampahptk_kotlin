package com.example.bank_sampah_app2023

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


class HomeFragment : Fragment() {

    private lateinit var pieChart: PieChart
    private lateinit var countBSText: TextView
    private lateinit var countSampahText: TextView

    private val mFirestore = FirebaseFirestore.getInstance()
    private val mSampahCount = mFirestore.collection("sampah")
    private val mBSCount = mFirestore.collection("banksampah")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_home, container, false)

        initView(view)

        return view
    }

    private fun initView(view: View) {
        pieChart = view.findViewById(R.id.pieChart)
        countBSText = view.findViewById(R.id.countBS)
        countSampahText = view.findViewById(R.id.countSampah)

        GlobalScope.launch(Dispatchers.Main) {
            val sampahCount = mSampahCount.get().await().size()
            val bsCount = mBSCount.get().await().size()

            countBSText.text = bsCount.toString()
            countSampahText.text = sampahCount.toString()

            // Data untuk Pie Chart
            val entries = ArrayList<PieEntry>()
            entries.add(PieEntry(sampahCount.toFloat(), "Sampah"))
            entries.add(PieEntry(bsCount.toFloat(), "Bank Sampah"))

            val dataSet = PieDataSet(entries, "Jumlah Data")
            dataSet.colors = mutableListOf(Color.rgb(0, 128, 0), Color.rgb(255, 140, 0))
            dataSet.valueTextColor = Color.WHITE
            dataSet.valueTextSize = 14f

            dataSet.valueFormatter = PercentFormatter(pieChart)

            val data = PieData(dataSet)
            pieChart.data = data

            pieChart.setUsePercentValues(true)

            pieChart.description.isEnabled = false
            pieChart.centerText = "Persentase"
            pieChart.setCenterTextSize(12f)
            pieChart.legend.isEnabled = true
            pieChart.setEntryLabelTextSize(10f)
            pieChart.animateXY(1500, 1500)

            pieChart.invalidate()
        }

    }

}