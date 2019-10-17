package com.example.integra.sharethemtest;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.jakewharton.rxbinding2.view.RxView;
import com.jakewharton.rxbinding2.widget.RxTextView;
import com.jakewharton.rxbinding2.widget.TextViewTextChangeEvent;

import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class RxAndroidDemoActivity2 extends AppCompatActivity {

    @BindView(R.id.tap_result)
    TextView tap_result;

    @BindView(R.id.tap_result_max_count)
    TextView tap_result_max_count;

    @BindView(R.id.layout_tap_area)
    Button layout_tap_area;

    @BindView(R.id.test_button)
    Button test_button;

    @BindView(R.id.tv_text)
    TextView tvText;
    @BindView(R.id.et_search)
    EditText et_search;
    private Disposable disposible;
    private Unbinder unbinder;
    private int maxTaps;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rx_android_demo2);
        unbinder = ButterKnife.bind(this);
        bufferTest();
        RxBindingClickTest();
        debounceTest();
    }

    private void bufferTest(){
        RxView.clicks(layout_tap_area).map(new Function<Object, Integer>() {
            @Override
            public Integer apply(Object o) throws Exception {
                return 1;
            }
        }).buffer(3, TimeUnit.SECONDS)

                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new Observer<List<Integer>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposible = d;
                    }

                    @Override
                    public void onNext(List<Integer> integers) {

                        Log.e(">>>>>>>>", "onNext: " + integers.size() + " taps received!");
                        if (integers.size() > 0) {
                            maxTaps = (integers.size() > maxTaps) ? integers.size() : maxTaps;
                            tap_result.setText(String.format("Recieved %d in 3secs", integers.size()));
                            tap_result_max_count.setText(String.format("Maximum of %d received in this session", maxTaps));
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(">>>>>>>>", "onError: " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        Log.e(">>>>>>>>", "onComplete");
                    }
                });
    }


    private void debounceTest(){
        compositeDisposable.add(RxTextView.textChangeEvents(et_search).skipInitialValue()
        .debounce(1,TimeUnit.SECONDS).subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribeWith(searchQuery()));
        tvText.setText("Search query will accumulate every 300 milliseconds.");
    }

    private DisposableObserver<TextViewTextChangeEvent> searchQuery() {

        return   new DisposableObserver<TextViewTextChangeEvent>(){

            @Override
            public void onNext(TextViewTextChangeEvent textViewTextChangeEvent) {

                tvText.setText("Query: "+textViewTextChangeEvent.text().toString());
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        };

    }

    private void RxBindingClickTest(){
        disposible=RxView.clicks(test_button).subscribe(new Consumer<Object>() {
            @Override
            public void accept(Object o) throws Exception {
                Toast.makeText(getApplicationContext(),"Button Clicked",Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
        disposible.dispose();
        compositeDisposable.clear();
    }
}
