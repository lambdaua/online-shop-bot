import com.fasterxml.jackson.databind.ObjectMapper
import java.io.File

class TemplateManager(private val objectMapper: ObjectMapper) {
    private val templateData: TemplateData =
        objectMapper.readValue(
            File("src/test/resources/template.json"),
            TemplateData::class.java
        )

    fun sections(): List<SectionData> {
        return templateData.sections
    }

    fun sectionNames(): List<String> {
        return templateData.sections.map { it.name }
    }

    fun section(id: Int): SectionData? {
        return templateData.sections.firstOrNull { it.id == id }
    }

    fun sectionByName(name: String): SectionData? {
        return templateData.sections.firstOrNull { it.name == name }
    }
}

data class TemplateData(val sections: List<SectionData>)

data class SectionData(
    val id: Int,
    val name: String,
    val stages: List<Stage>,
    val data: List<Data>,
    val postStages: List<PostStage> = listOf()
) {
    fun postStages(): List<PostStage> {
        return postStages
    }

    fun postStageByTag(id: Int): PostStage? {
//        postStages.forEach { postStage ->
//            if (postStage.tags.any { it.id == id }) return postStage
//        }
        return postStages.first { it.tags.any { it.id == id } }
    }

    fun nextPostStageFor(id: Int): PostStage? {
        //depends_on
        if (postStages.isNotEmpty()) {
            return postStages.firstOrNull { it.dependsOn.contains(id) }
        }
        return null
    }

    fun product(id: Int): Data? {
        return data.firstOrNull { it.id == id }
    }

    fun validProduct(id: Int): Data? {
        if (id <= 0) return data.first()
        if (id > data.size) return data.last()
        return null
    }

    fun totalProducts(): Int {
        return data.size
    }

    fun stage(): List<Stage> {
        return stages
    }

    fun nextStage(id: Int): Stage? {
        if (stages.isNotEmpty()) {
            return stages.firstOrNull { it.id == id }
        }
        return null
    }

    fun nextPossibleStage(tags: List<String>, currentStage: Stage): Stage? {
        val stage = stages.filter { it.id == currentStage.id + 1 }.firstOrNull() ?: return null

        val filteredData = data.filter { it.tags.containsAll(tags) }
        val availableProductsSet = filteredData.flatMap { it.tags }.toSet()

        return if (availableProductsSet.containsAll(stage.tags)) {
            stage
        } else {
            nextPossibleStage(tags, stage)
        }
    }

    fun filteredProducts(tags: List<String>): List<Data> {
        return data.filter { it.tags.containsAll(tags) }
    }
}

data class Stage(
    val id: Int,
    val name: String,
    val text: String,
    val tags: List<String> = listOf()
)

data class Data(
    val id: Int? = null,
    val name: String,
    val description: String,
    val price: String,
    val imageUrl: String,
    val tags: List<String> = listOf()
)

data class PostStage(
    val id: Int,
    val name: String,
    val tags: List<Tag> = listOf(),
    val dependsOn: List<Int> = listOf()
)

data class Tag(
    val id: Int,
    val name: String
)
/*
manager.section(<sectionId>).postStage().first()
manager.section(<sectionId>).nextPostStageFor(<postStageId>)
manager.section(<sectionId>).nextPostStageForTag(<postStageTagId>)
manager.section("sectionName").product(<id>)
manager.section("sectionName").totalProducts()
manager.sections()
manager.section(<sectionId>).stage().first()
manager.section(<sectionId>).stage(<stageId>+1)
manager.section(<sectionId>).availableTags(<listOfEnteredTags>)
*/