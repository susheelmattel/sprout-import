package com.sproutling.apiservices

import com.sproutling.apiservices.EventApiBuilder.EventApi
import com.sproutling.pojos.CreateEventRequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Created by subram13 on 11/7/17.
 */
class EventApiBuilder : ApiBuilder<EventApi>() {

    companion object {
        const val CREATE_EVENT_URL = "timeline/v1/events"
    }

    override fun getMockInstance(): EventApi {
        return MockEventApi()
    }

    override fun getService(): Class<EventApi> = EventApi::class.java


    interface EventApi {
        companion object {
            const val EVENT_NAP = "nap"
        }

        @POST(CREATE_EVENT_URL)
        fun createEvent(@Body createEventRequestBody: CreateEventRequestBody): Call<ResponseBody>
    }

    class MockEventApi : EventApi {
        override fun createEvent(createEventRequestBody: CreateEventRequestBody): Call<ResponseBody> {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

    }
}