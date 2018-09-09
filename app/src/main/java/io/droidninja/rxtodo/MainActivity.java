package io.droidninja.rxtodo;

import android.content.Context;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import io.droidninja.rxtodo.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;

    private static final String LIST = "list";
    TodoAdapter adapter;

    TodoList list;
    TodoListFilter filter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        if (savedInstanceState != null) {
            list = new TodoList(savedInstanceState.getString(LIST));
        } else {
            list = new TodoList(getSharedPreferences("data", Context.MODE_PRIVATE).getString(LIST, null));
        }

        // setup the Filter which allows us to get the data we want to display
        filter = new TodoListFilter(list);

        // setup the Adapter, this contains a callback when an item is checked/unchecked
        adapter = new TodoAdapter(this, new TodoCompletedChangeListener() {
            @Override
            public void onTodoCompletedChanged(Todo todo) {
                list.toggle(todo);
                adapter.onTodoListChanged(filter.getFilteredData());
            }
        });

        // setup the list with the adapter
        binding.rc.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        binding.rc.setAdapter(adapter);

        // setup adding new items to the list
        binding.addTodoContainer.requestFocus(); // ensures the edittext isn't focused when entering the Activity
        findViewById(R.id.btn_add_todo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String item = binding.addTodoInput.getText().toString();

                // ensure we don't add empty items
                if (!TextUtils.isEmpty(item.trim())) {

                    // update our list
                    list.add(new Todo(item, false));

                    // update the adapter with the latest filtered data
                    adapter.onTodoListChanged(filter.getFilteredData());

                    // clear input, remove focus, and hide keyboard
                    binding.addTodoInput.setText("");
                    findViewById(R.id.add_todo_container).requestFocus();
                    dismissKeyboard();
                }
            }
        });

        binding.spinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,
                new String[]{"All", "Incomplete", "Completed"}));
        binding.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filter.setFilterMode(position);
                adapter.onTodoListChanged(filter.getFilteredData());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                filter.setFilterMode(TodoListFilter.ALL);
                adapter.onTodoListChanged(filter.getFilteredData());
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(LIST, list.toString());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        SharedPreferences.Editor editor = getSharedPreferences("data", Context.MODE_PRIVATE).edit();
        editor.putString(LIST, list.toString());
        editor.apply();
    }

    private void dismissKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(binding.addTodoInput.getWindowToken(), 0);
    }
}
