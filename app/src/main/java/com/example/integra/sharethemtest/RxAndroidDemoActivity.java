package com.example.integra.sharethemtest;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.integra.sharethemtest.model.Address;
import com.example.integra.sharethemtest.model.Note;
import com.example.integra.sharethemtest.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import rx.Subscriber;
import rx.observables.MathObservable;

public class RxAndroidDemoActivity extends AppCompatActivity {

    private static final String TAG = ">>>>>>>";
    private Disposable disposable;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rx_android_demo);
       /* doRxStuff();
        doRxStuffWithFilterOperator();
        doRxStuffWithCompositeDisposible();
        doRxStuffWithCustomDataType();
        doRxStuffFindThePrimeNosRangingUpto(10000);
        doRxStuffFlatMap();*/

        rxMathTest();

    }

    private void doRxStuff() {
        Observable<String> animalsObservable = Observable.just("Whale", "Elephant", "Lion", "Chimpanzee", "Human", "Ant");
        animalsObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getAnimalsObserver());
    }

    private void doRxStuffWithFilterOperator() {
        Observable<String> animalsObservable = Observable.just("Whale", "Elephant", "Lion", "Chimpanzee", "Human", "Ant");
        animalsObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .filter(new Predicate<String>() {
                    @Override
                    public boolean test(String s) throws Exception {
                        return s.toLowerCase().contains("e");
                    }
                }).subscribeWith(getAnimalsObserver());

    }

    private void doRxStuffWithCompositeDisposible() {
        Observable<String> animalsObservable = getAnimalsObservable();
        DisposableObserver<String> animalsObserver = getAnimalsObserver1();
        DisposableObserver<String> animalObserverAllCap = getAnimalsAllCapObserver();

        compositeDisposable.add(animalsObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).filter(new Predicate<String>() {
                    @Override
                    public boolean test(String s) throws Exception {

                        return s.toLowerCase().contains("d");
                    }
                }).subscribeWith(animalsObserver));


        compositeDisposable.add(animalsObservable.subscribeOn(Schedulers.io()
        ).observeOn(AndroidSchedulers.mainThread()).filter(new Predicate<String>() {
            @Override
            public boolean test(String s) throws Exception {
                return s.toLowerCase().contains("d");
            }
        }).map(new Function<String, String>() {

            @Override
            public String apply(String s) throws Exception {
                return s.toUpperCase();
            }
        }).subscribeWith(animalObserverAllCap));


    }

    private void doRxStuffWithCustomDataType() {
        Observable<Note> notesObservable = getNotesObservable();
        DisposableObserver<Note> notesObserver = getNotesObserver();
        compositeDisposable.add(notesObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).filter(new Predicate<Note>() {
                    @Override
                    public boolean test(Note note) throws Exception {
                        return note.getNote().contains("a");
                    }
                }).map(new Function<Note, Note>() {
                    @Override
                    public Note apply(Note note) throws Exception {
                        note.setNote(note.getNote().toUpperCase());
                        return note;
                    }
                }).subscribeWith(notesObserver));

    }

    private void doRxStuffFindThePrimeNosRangingUpto(int upperLimit) {
        Observable.range(1, upperLimit).subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .filter(new Predicate<Integer>() {
                    @Override
                    public boolean test(Integer integer) throws Exception {
                        int count = 0;
                        for (int i = 1; i <= integer; i++) {
                            if (integer % i == 0) {
                                count++;
                            }

                        }
                        return count == 2;
                    }
                }).map(new Function<Integer, String>() {
            @Override
            public String apply(Integer integer) throws Exception {
                return integer + " is an Prime No.";
            }
        }).subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(String s) {

                Log.d(TAG, s);
            }

            @Override
            public void onError(Throwable e) {
                Log.d(TAG, "onError" + "\n error:" + e.getMessage());
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "onComplete");
            }
        });
    }


    private void doRxStuffFlatMap() {

        getUsersObservable().observeOn(AndroidSchedulers.mainThread())
                .flatMap(new Function<User, ObservableSource<User>>() {
                    @Override
                    public ObservableSource<User> apply(User user) throws Exception {
                        return getAddressObservable(user);
                    }
                }).subscribe(new Observer<User>() {

            @Override
            public void onSubscribe(Disposable d) {
                disposable = d;
            }

            @Override
            public void onNext(User user) {
                Log.d(TAG, user.getName() + "," + user.getGender() + "," + user.getAddress().getAddress());

            }

            @Override
            public void onError(Throwable e) {
                Log.d(TAG, "onError" + "\n error:" + e.getMessage());
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "onComplete");
            }
        });

    }


    private Observable<User> getUsersObservable() {

        String[] maleUsers = {"Narendra Modi", "Vladimir Putin", "Donald Trump", "Imran Khan", "Kim Jong-Un"};
        final List<User> users = new ArrayList<>();
        for (String name : maleUsers) {
            User user = new User();
            user.setName(name);
            user.setGender("Male");
            users.add(user);
        }

        return Observable.create(new ObservableOnSubscribe<User>() {
            @Override
            public void subscribe(ObservableEmitter<User> emitter) throws Exception {
                for (User user : users) {
                    if (!emitter.isDisposed()) {
                        emitter.onNext(user);
                    }
                }
                if (!emitter.isDisposed()) {
                    emitter.onComplete();
                }


            }
        }).subscribeOn(Schedulers.io());

    }

    private Observable<User> getAddressObservable(final User user) {
        final String[] addresses = new String[]{
                "1600 Amphitheatre Parkway, Mountain View, CA 94043",
                "2300 Traverwood Dr. Ann Arbor, MI 48105",
                "500 W 2nd St Suite 2900 Austin, TX 78701",
                "355 Main Street Cambridge, MA 02142"
        };

        return Observable.create(new ObservableOnSubscribe<User>() {
            @Override
            public void subscribe(ObservableEmitter<User> emitter) throws Exception {
                Address address = new Address();
                address.setAddress(addresses[new Random().nextInt(3)]);
                if (!emitter.isDisposed()) {
                    user.setAddress(address);

                    int sleepTime = new Random().nextInt(1000) + 500;
                    Thread.sleep(sleepTime);
                    emitter.onNext(user);
                    emitter.onComplete();

                }
            }
        }).subscribeOn(Schedulers.io());


    }


    private DisposableObserver<Note> getNotesObserver() {
        return new DisposableObserver<Note>() {
            @Override
            public void onNext(Note note) {
                Log.d(TAG, "id:" + note.getId() + "||" + "Note:" + note.getNote());
            }

            @Override
            public void onError(Throwable e) {
                Log.d(TAG, "onError" + "\n error:" + e.getMessage());
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "onComplete");
            }
        };
    }


    private Observable<Note> getNotesObservable() {
        final List<Note> notes = prepareNotes();
        return Observable.create(new ObservableOnSubscribe<Note>() {
            @Override
            public void subscribe(ObservableEmitter<Note> emitter) throws Exception {

                for (Note note : notes) {
                    if (!emitter.isDisposed()) {
                        emitter.onNext(note);
                    }
                }
                if (!emitter.isDisposed())
                    emitter.onComplete();
            }


        });
    }

    private List<Note> prepareNotes() {
        List<Note> notes = new ArrayList<>();
        notes.add(new Note(1, "Hi"));
        notes.add(new Note(2, "Today is Friday"));
        notes.add(new Note(3, "Tomorrow is the Weekend"));
        notes.add(new Note(4, "I will go to Iskon"));
        notes.add(new Note(5, "I will Watch movie"));
        notes.add(new Note(6, "I will wash clothes"));
        notes.add(new Note(7, "I will complete a book"));
        notes.add(new Note(8, "I may roam in a mall"));
        return notes;

    }


    private Observer<String> getAnimalsObserver() {
        return new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.d(TAG, "onSubscribe");
                disposable = d;
            }

            @Override
            public void onNext(String s) {
                Log.d(TAG, s);
            }

            @Override
            public void onError(Throwable e) {
                Log.d(TAG, "onError" + "\n error:" + e.getMessage());
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "onComplete");
            }
        };
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        //   disposable.dispose();
        compositeDisposable.clear();
    }

    private Observable<String> getAnimalsObservable() {

        return Observable.fromArray("Whale", "Elephant", "Lion", "Chimpanzee", "Human", "Monkey", "Cheetah", "Tiger", "Shark", "Rabbit"
                , "Dog", "Cat", "Parrot", "Horse", "Donkey", "Duck", "Sparrow", "Kingfisher");
    }

    private DisposableObserver<String> getAnimalsObserver1() {
        return new DisposableObserver<String>() {
            @Override
            public void onNext(String s) {
                Log.d(TAG, s);
            }

            @Override
            public void onError(Throwable e) {
                Log.d(TAG, "onError" + "\n error:" + e.getMessage());
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "onComplete");
            }
        };
    }

    private DisposableObserver<String> getAnimalsAllCapObserver() {
        return new DisposableObserver<String>() {
            @Override
            public void onNext(String s) {
                Log.d(TAG, s);
            }

            @Override
            public void onError(Throwable e) {
                Log.d(TAG, "onError" + "\n error:" + e.getMessage());
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "onComplete");
            }
        };
    }

    private void rxMathTest(){
        Integer[] nos= {1,23,2134,12,54,456,643,31,234};
        rx.Observable observable = rx.Observable.from(nos);
        MathObservable.max(observable).subscribe(new Subscriber<Integer>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Integer integer) {
                Log.d(TAG, "Max value: " + integer);
            }
        });

    }
}
