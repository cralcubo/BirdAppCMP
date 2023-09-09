import dev.icerock.moko.mvvm.viewmodel.ViewModel
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import model.BirdImage

data class BirdsUiState(val images:List<BirdImage> = emptyList())
class BirdsViewModel: ViewModel() {
    private val mutableState = MutableStateFlow(BirdsUiState())
    val uiState: StateFlow<BirdsUiState> by ::mutableState

    init {
        updateImages()
    }

    override fun onCleared() {
        httpClient.close()
    }

    private val httpClient = HttpClient {
        expectSuccess = true // if request was not successful an exception is thrown
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            })
        }
    }

    private suspend fun getImages(): List<BirdImage> {
        return httpClient
            .get("https://sebastianaigner.github.io/demo-image-api/pictures.json")
            .body()
    }

    fun updateImages() {
        viewModelScope.launch {
            mutableState.update { BirdsUiState(getImages()) }
        }
    }
}

