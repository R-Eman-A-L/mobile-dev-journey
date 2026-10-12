package com.example.streakbook

fun findHighestInt(num: List<Int>): Int?{
    if(num.isEmpty()) return null;
    var maxNumber = 0;
    for(i in num.indices){
        if(num[i] > maxNumber) maxNumber = num[i];
    }

    return maxNumber;
}