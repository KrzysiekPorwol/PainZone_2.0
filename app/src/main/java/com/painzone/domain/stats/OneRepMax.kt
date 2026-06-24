package com.painzone.domain.stats

// Estimated one-rep max (1RM) per the Epley formula: weight × (1 + reps / 30).
// Pure function over a single set's reps/weight — Stats Lite picks the best set as
// MAX(estimate) within the active filter window (US-6, M4.4). Formula choice: see
// 05-domain-session.md#1RM-estimate (Epley, no special-case for reps == 1 — the
// documented formula is applied verbatim so a 1-rep set reads slightly above the
// raw weight, consistent across all sets being ranked).
fun estimateOneRepMax(weight: Double, reps: Int): Double {
    require(reps >= 1) { "reps must be >= 1" }
    require(weight >= 0) { "weight must be >= 0" }
    return weight * (1 + reps / 30.0)
}

// Convenience over the Stats Lite read projection used to rank sets (M4.4).
fun StatsSet.estimatedOneRepMax(): Double = estimateOneRepMax(weight, reps)
