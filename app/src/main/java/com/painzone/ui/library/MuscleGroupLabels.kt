package com.painzone.ui.library

import com.painzone.domain.exercise.MuscleGroup

val MuscleGroup.labelPl: String
    get() = when (this) {
        MuscleGroup.Chest -> "Klatka"
        MuscleGroup.Back -> "Plecy"
        MuscleGroup.Legs -> "Nogi"
        MuscleGroup.Biceps -> "Biceps"
        MuscleGroup.Triceps -> "Triceps"
        MuscleGroup.Shoulders -> "Barki"
        MuscleGroup.Abs -> "Brzuch"
    }