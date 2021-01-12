package hash2019

import java.io.File
import java.util.*

val input = "b_lovely_landscapes.txt"
val output = "out_${hash2020.input}"

fun main(args: Array<String>) {
    val scanner = Scanner(File(hash2020.input))

    var photoId = 0
    val photos = LinkedList<Photo>()

    val N = Integer.parseInt(scanner.nextLine())
    (0 until N).forEach {
        val line = scanner.nextLine().split(" ")
        val type = line[0]
        val count = Integer.parseInt(line[1])
        val tags = (2 until count + 2).map {
            line[it]
        }
        photos.add(Photo(photoId++, type, tags))
    }

    photos.sortBy { it.tags.size }

    val slideShow = Slideshow()

    val removeFirst = photos.removeFirst()
    slideShow.add(Slide(removeFirst))

//    while (photos.size > 0) {
//        val needVertical = slideShow.needVertical()
//
//        println("Left photos ${photos.size}")
//        val list = arrayListOf<hash2020.Photo>()
//        for (photo in photos) {
//            if (list.size > 150)
//                break
//            if (needVertical) {
//                if (photo.type == hash2020.Slideshow.VERTICAL) {
//                    list.add(photo)
//                }
//            } else {
//                list.add(photo)
//            }
//        }
//
//        val closest = photos.stream().parallel().sorted((Comparator<hash2020.Photo> { photo1, photo2 ->
//            var photo1Score = Math.min(Math.min((slideShow.getLastTags() - photo1.tags).size, slideShow.getLastTags().intersect(photo1.tags).size), (photo1.tags - slideShow.getLastTags()).size)
//            var photo2Score = Math.min(Math.min((slideShow.getLastTags() - photo2.tags).size, slideShow.getLastTags().intersect(photo2.tags).size), (photo2.tags - slideShow.getLastTags()).size)
//            photo2Score.compareTo(photo1Score)
//        })).findFirst().get()
//
//        if (!needVertical || (needVertical && closest.type == hash2020.Slideshow.VERTICAL)) {
//            photos.remove(closest)
//            if (needVertical)
//                slideShow.lastSlide().photos.add(closest)
//            else
//                slideShow.add(hash2020.Slide(closest))
//        } else {
//            photos.remove()
//        }
//    }

    while (photos.size > 0) {
        val needVertical = slideShow.needVertical()

        println("Left photos ${photos.size}")

        val list = arrayListOf<Photo>()
        for (photo in photos) {
            if (list.size > 10000)
                break
            if (needVertical) {
                if (photo.type == Slideshow.VERTICAL) {
                    if (slideShow.getLastTags().intersect(photo.tags).size < Math.abs(slideShow.getLastTags().size - photo.tags.size))
                        list.add(photo)
                }
            } else {
                if (slideShow.getLastTags().intersect(photo.tags).size < Math.abs(slideShow.getLastTags().size - photo.tags.size))
                    list.add(photo)
            }
        }

        val find = list.minWith((Comparator { photo1, photo2 ->
            val photo1Score = Math.min(Math.min((slideShow.getLastTags() - photo1.tags).size, slideShow.getLastTags().intersect(photo1.tags).size), (photo1.tags - slideShow.getLastTags()).size)
            val photo2Score = Math.min(Math.min((slideShow.getLastTags() - photo2.tags).size, slideShow.getLastTags().intersect(photo2.tags).size), (photo2.tags - slideShow.getLastTags()).size)
            photo2Score.compareTo(photo1Score)
        }))
        val closest: Photo? = if (find != null) {
            find
        } else {
            photos.find {
                if (needVertical)
                    return@find it.type == Slideshow.VERTICAL
                return@find slideShow.getLastTags().intersect(it.tags).isNotEmpty()
            }
        }
        if (closest == null) {
            slideShow.slides.removeAt(slideShow.slides.size - 1)
        } else {
            if (!needVertical || (needVertical && closest.type == Slideshow.VERTICAL)) {
                photos.remove(closest)
                if (needVertical)
                    slideShow.lastSlide().photos.add(closest)
                else
                    slideShow.add(Slide(closest))
            } else {
                photos.remove()
            }
        }

    }


    File(hash2020.output).printWriter().use { out ->
        out.println(slideShow.slides.size)
        slideShow.slides.forEach {
            out.println(it.photos.map { it.id }.joinToString(separator = " "))
        }
    }

}


class Photo(val id: Int, val type: String, val tags: List<String>) {

    val scores = hashMapOf<String, Int>()

    override fun toString(): String {
        return "Phone #$id ${if (type == "H") "horizontal" else "vertical"} with tags $tags"
    }


}

class Slide(photo: Photo) {
    val photos: ArrayList<Photo> = arrayListOf()

    init {
        photos.add(photo)
    }

}

class Slideshow() {

    val slides: ArrayList<Slide> = arrayListOf()

    companion object {
        const val HORIZONTAL = "H"
        const val VERTICAL = "V"
        const val ANY = "ANY"
    }

    fun getLastPhoto(): Photo {
        return slides.last().photos.last()
    }

    fun getLastTags(): List<String> {
        return slides.last().photos.flatMap { it.tags }
    }


    fun needVertical(): Boolean {
        val lastSlide = slides.last()
        if (lastSlide.photos.size == 2)
            return false
        return lastSlide.photos[0].type.equals(VERTICAL)
    }

    fun lastSlide(): Slide {
        return slides.last()
    }

    fun add(slide: Slide) {
        slides.add(slide)
    }

}