package com.example.tipculator.util

fun calculateTotalTip(totalBill: String, tipPercentage: Int): Double {
    /*
    Below statement order is crucial to bug-free running,
    especially progress bar without bill entered in "InputField".
     */
    return if(totalBill.isNotEmpty()&&totalBill.toDouble()>0.0){
        (totalBill.toDouble()*tipPercentage)/100.0
    }else{
        0.0
    }
}

fun calculateTotalPerPerson(totalBill: String,peopleAmount:Int,tipPercentage:Int):Double{
    return if(totalBill.isNotEmpty()){
        val tip = calculateTotalTip(totalBill,tipPercentage)
        (totalBill.toDouble()+tip)/peopleAmount
    }else{
        0.0
    }
}