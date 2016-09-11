package solutions.coub.com.flowers

import android.os.Looper
import rx.Observable
import rx.Scheduler
import rx.Single
import rx.android.schedulers.AndroidSchedulers
import rx.subjects.Subject

/**
 * Created by punksta on 28.07.16.
 */
@SuppressWarnings("unchecked")
object RxEventBus {
    val  events: Subject<Any, Any> by lazy { rx.lang.kotlin.PublishSubject<Any>() }
    private val busScheduler: Scheduler by lazy {  AndroidSchedulers.mainThread() }


//    fun <T> postEvent(event: T) {
//        if (Looper.myLooper() == Looper.getMainLooper()) {
//            events.onNext(event)
//        } else {
//            Single.just(event).observeOn(busScheduler).doOnSuccess { events.onNext(it) }.subscribe()
//        }
//    }

    fun <T> postEvent(event: T) {
        Single.just(event).observeOn(busScheduler).doOnSuccess { events.onNext(it) }.subscribe()
    }

    fun observe(): rx.Observable<Any>
            = events.observeOn(busScheduler)


    @JvmName("observeType")
    inline fun <reified T> observe(): rx.Observable<T>
            = observe().filter { it is T } as rx.Observable<T>


    fun observe(vararg classes: Class<*>): rx.Observable<Any> {
        val predicate = {event: Any -> classes.find { it.isInstance(event) } != null}
        return observe().filter(predicate)
    }

    @JvmName("observeVarargType")
    fun <T> observe(vararg classes: Class<out T>): rx.Observable<out T> {
        val predicate = {event: Any -> classes.find { it.isInstance(event) } != null}
        return observe().filter(predicate) as Observable<T>
    }

}