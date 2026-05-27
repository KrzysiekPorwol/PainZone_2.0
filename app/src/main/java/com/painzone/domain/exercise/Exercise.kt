package com.painzone.domain.exercise

import java.time.Instant

data class Exercise(
    val id: Long,
    val name: String,
    val muscleGroup: MuscleGroup,
    val createdAt: Instant,
    val deletedAt: Instant?,
) {
    init {
        require(name == name.trim()) { "name must be trimmed" }
        require(name.isNotEmpty()) { "name must be non-blank" }
        require(deletedAt == null || !deletedAt.isBefore(createdAt)) {
            "deletedAt must be >= createdAt"
        }
    }

    val isActive: Boolean get() = deletedAt == null

    fun rename(newName: String): Exercise = copy(name = newName.trim())

    fun softDelete(at: Instant): Exercise {
        check(isActive) { "exercise is already soft-deleted" }
        return copy(deletedAt = at)
    }

    companion object {
        fun create(name: String, muscleGroup: MuscleGroup, now: Instant): Exercise =
            Exercise(
                id = 0L,
                name = name.trim(),
                muscleGroup = muscleGroup,
                createdAt = now,
                deletedAt = null,
            )
    }
}
