package blue.aodev.animeultimetv.domain.model

data class Episode(
        val title: String,
        val number: Int,
        val imageUrl: String,
        val videoUrl: String,
        val hdVideoUrl: String,
        val duration: Int
)
