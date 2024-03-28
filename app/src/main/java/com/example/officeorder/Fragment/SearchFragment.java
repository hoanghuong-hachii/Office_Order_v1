package com.example.officeorder.Fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.officeorder.Adapter.SearchAdapter;
import com.example.officeorder.Config.ApiClient;
import com.example.officeorder.Config.VariableEnvironment;
import com.example.officeorder.Config.getToken;
import com.example.officeorder.Model.Product;
import com.example.officeorder.Model.UserSingleton;
import com.example.officeorder.R;
import com.example.officeorder.Service.ApiService;
import com.google.android.material.slider.RangeSlider;
import com.google.android.material.textfield.TextInputEditText;

import java.text.DecimalFormat;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class SearchFragment extends Fragment {

    private TextView tvloc;
    private TextInputEditText searchView;
    private ImageButton back;
    private RecyclerView recyclerView;
    private SearchAdapter adapter;
    private VariableEnvironment variableEnvironment = new VariableEnvironment("value");
    private static final String IMAGES_DIRECTORY = "images";
    private Spinner sortDirectionSpinner;
    private String currentKeyword;
    private String currentSort = "asc";
    private float currentMinValue = 0f;
    private float currentMaxValue = 1000000f;

    private ApiService apiService;
    private String from ;
    private String to  ;
    private String idUser;

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_search, container, false);

        UserSingleton userSingleton = UserSingleton.getInstance();
        idUser  = userSingleton.getUserId();

        apiService = ApiClient.getApiService();
        //=================
        searchView = v.findViewById(R.id.editTextsearch);
        back = v.findViewById(R.id.backButton);
        recyclerView = v.findViewById(R.id.rvseach);

        tvloc = v.findViewById(R.id.loc);

        sortDirectionSpinner = v.findViewById(R.id.sortDirectionSpinner);
        ArrayAdapter<CharSequence> sortDirectionAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.sort_directions, R.layout.spinner_item);
        sortDirectionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortDirectionSpinner.setAdapter(sortDirectionAdapter);
        sortDirectionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedSort = parent.getItemAtPosition(position).toString();

                if (selectedSort.equals("Thấp đến cao")) {
                    selectedSort = "asc";
                }
                else selectedSort = "desc";
                if (!selectedSort.equals(currentSort)) {
                    currentSort = selectedSort;
                    performSorting();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        searchView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (searchView.getRight() -
                            searchView.getCompoundDrawables()[2].getBounds().width())) {

                        searchView.setText("");
                        return true;
                    }
                }
                return false;
            }
        });

        searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Update currentKeyword
                currentKeyword = s.toString();
                SearchDataTask(currentKeyword);
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        tvloc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPriceRangeDialog();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() != null)
                    getActivity().onBackPressed();
            }
        });

        adapter = new SearchAdapter(getContext(), getFragmentManager());

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        gridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(adapter);

        return v;
    }

    private void showPriceRangeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_price_range, null);
        builder.setView(dialogView);

        // Khởi tạo RangeSlider và thiết lập giá trị mặc định
        RangeSlider rangeSlider = dialogView.findViewById(R.id.rangeSlider);
        rangeSlider.setValueFrom(0); // Giá trị tối thiểu
        rangeSlider.setValueTo(1000000); // Giá trị tối đa
        rangeSlider.setValues(currentMinValue, currentMaxValue);

        // Hiển thị giá trị mặc định trên tvMin và tvMax
        TextView tvMin = dialogView.findViewById(R.id.tvMin);
        TextView tvMax = dialogView.findViewById(R.id.tvMax);

        updateMinMaxValues(rangeSlider, tvMin, tvMax); // Cập nhật giá trị ban đầu

        //khi người dùng thay đổi giá trị trên RangeSlider
        rangeSlider.addOnSliderTouchListener(new RangeSlider.OnSliderTouchListener() {
            @Override
            public void onStartTrackingTouch(@NonNull RangeSlider slider) {
            }
            @Override
            public void onStopTrackingTouch(@NonNull RangeSlider slider) {
                updateMinMaxValues(slider, tvMin, tvMax);
            }
        });
        // Sự kiện click vào tvMin
        tvMin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMinMaxInputDialog(true, rangeSlider, tvMin, tvMax);
            }
        });
        // Sự kiện click vào tvMax
        tvMax.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMinMaxInputDialog(false, rangeSlider, tvMin, tvMax);
            }
        });
        // Nút "Cancel"
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        // Nút "OK"
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Lấy giá trị từ RangeSlider
                float minValue = rangeSlider.getValues().get(0);
                float maxValue = rangeSlider.getValues().get(1);
                // Lưu trữ giá trị hiện tại
                currentMinValue = minValue;
                currentMaxValue = maxValue;
                // Chuyển đổi giá trị sang String
                from = String.valueOf(minValue);
                to = String.valueOf(maxValue);
                performSorting();
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void updateMinMaxValues(RangeSlider rangeSlider, TextView tvMin, TextView tvMax) {
        DecimalFormat decimalFormat = new DecimalFormat("#,###");
        tvMin.setText(decimalFormat.format(rangeSlider.getValues().get(0)));
        tvMax.setText(decimalFormat.format(rangeSlider.getValues().get(1)));
    }

    private void showMinMaxInputDialog(final boolean isMinValue, final RangeSlider rangeSlider, final TextView tvMin, final TextView tvMax) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(isMinValue ? "Nhập giá trị" : "Nhập giá trị");

        // Khởi tạo EditText để nhập số
        final EditText inputEditText = new EditText(getContext());
        inputEditText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        builder.setView(inputEditText);

        // Nút "OK"
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String inputValue = inputEditText.getText().toString();
                if (!inputValue.isEmpty()) {
                    float newValue = Float.parseFloat(inputValue);
                    List<Float> currentValues = rangeSlider.getValues();
                    if (isMinValue) {
                        rangeSlider.setValues(newValue, currentValues.get(1));
                    } else {
                        rangeSlider.setValues(currentValues.get(0), newValue);
                    }
                    updateMinMaxValues(rangeSlider, tvMin, tvMax);
                }
                dialog.dismiss();
            }
        });

        // Nút "Cancel"
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Hiển thị hộp thoại
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void performSorting() {
        if (currentKeyword != null && !currentKeyword.isEmpty()) {
           SearchDataTask(currentKeyword);
        }
    }
    private void SearchDataTask(String keyword){
        //=========Access Token=========
        UserSingleton userSingleton = UserSingleton.getInstance();
        getToken get = new getToken();
        get.test();
        String accessToken = userSingleton.getAccessToken();
        ////================
        Call<List<Product>> call = apiService.searchProduct(keyword, currentSort, from, to,"Bearer "+ accessToken);
        call.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Product> dataList = response.body();
                    adapter.setData(dataList);
                } else {
                    // Handle unsuccessful response
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                // Handle network errors
            }
        });
    }

}