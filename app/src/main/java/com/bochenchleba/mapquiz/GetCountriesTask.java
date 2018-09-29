package com.bochenchleba.mapquiz;

import android.os.AsyncTask;
import android.util.Log;

import com.bochenchleba.mapquiz.db.DbHandler;
import com.bochenchleba.mapquiz.db.Record;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by bochenchleba on 23/02/18.
 */

public class GetCountriesTask extends AsyncTask<GetCountriesTask.TaskParams, Void, List<String>> {

    private DbHandler dbHandler;

    public GetCountriesTask(DbHandler dbHandler) {
        super();

        this.dbHandler = dbHandler;
    }


    @Override
    protected List<String> doInBackground(TaskParams... parameters) {

        List<String> result = new ArrayList<>();

        switch (parameters[0].taskId){

            case Fields.TASK_GET_MATCHING_COUNTRIES:

                result = dbHandler.getCountries(parameters[0].param0, parameters[0].param1);
                break;

            case Fields.TASK_GET_COORDINATES:

                result = dbHandler.getCoordinates(parameters[0].param1);
                break;
        }

        return result;
    }

    protected void onPostExecute(List<String> result) {

    }


    public static class TaskParams {
        int taskId;
        Set<String> param0;
        String param1;

        TaskParams(int taskId, Set<String> param0, String param1) {
            this.taskId = taskId;
            this.param0 = param0;
            this.param1 = param1;
        }

    }

}
