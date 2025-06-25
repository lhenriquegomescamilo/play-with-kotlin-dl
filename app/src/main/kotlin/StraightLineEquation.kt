package com.camilo.app

import org.jetbrains.kotlinx.dl.api.core.Sequential
import org.jetbrains.kotlinx.dl.api.core.layer.core.Dense
import org.jetbrains.kotlinx.dl.api.core.layer.core.Input
import org.jetbrains.kotlinx.dl.api.core.loss.Losses
import org.jetbrains.kotlinx.dl.api.core.metric.Metrics
import org.jetbrains.kotlinx.dl.api.core.optimizer.Adam
import org.jetbrains.kotlinx.dl.dataset.OnHeapDataset

class StraightLineEquation {
}

fun main() {
    val rawInputs = FloatArray(100) { it.toFloat() }
    val rawLabels = FloatArray(100) { x -> 2f * x + 1f }

    // Normalization
    val inputMax = rawInputs.max()
    val labelMax = rawLabels.max()

    val normalizedInputs = rawInputs.map { floatArrayOf(it / inputMax) }.toTypedArray()
    val normalizedLabels = rawLabels.map { it / labelMax }.toFloatArray()

    val dataset = OnHeapDataset.create(
        features = normalizedInputs,
        labels = normalizedLabels
    )

    val model = Sequential.of(
        Input(1),
        Dense(1)
    )

    model.use {
        it.compile(
            optimizer = Adam(learningRate = 0.01f), // slightly faster convergence
            loss = Losses.MSE,
            metric = Metrics.MSE
        )

        it.fit(dataset = dataset, epochs = 10_000)

        // Predict for x = 5 → normalize to model scale
        val x = 5.0f
        val normalizedX = floatArrayOf(x / inputMax)
        val normalizedPrediction = it.predictSoftly(normalizedX)[0]

        // Denormalize prediction
        val prediction = normalizedPrediction * labelMax

        println("Expected y = 2*5 + 1 = 11.0")
        println("Predicted y = $prediction")
    }
}