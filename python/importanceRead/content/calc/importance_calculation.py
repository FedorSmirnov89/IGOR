"""

File containing the methods used for the calculation of the importance of the SAT variables

"""

import pandas as pd
import json
from sklearn.tree import DecisionTreeRegressor
import time
import logging

# Input syntax
objective_in_string = 'Objective Function'
objective_separator_in = ':'

# Output syntax
variable_id_out_string = 'variableIds'
importance_out_string = 'importanceValues'

# Importance summary
summary_options = ['avg', 'max']


class VariableImportanceCalculator:
    """
    Class used for the calculation of the variable importance.

    Class Attributes:
    _memory: [data frame] a data frame containing the individuals seen up to this point. Not used if _has_memory
    is false
    """
    def __init__(self, has_memory=False, memory_length=-1, importance_threshold=.001, summary_option='max'):
        """
        :param has_memory: [boolean] specifies whether the information calculation considers the individuals seen
        until the current call
        :param memory_length: [int] specifies the number of remembered calls to the calculate_importance method.
        Not used if _has_memory is false. Set to -1 if all individuals which were ever processed are to be stored
        :param importance_threshold: the threshold for the importance of variables. Anything with an importance below
        this value is not considered important
        :param summary_option: the way to summarize the importance w.r.t. different objectives
        """
        self._has_memory = has_memory
        self._memory_length = memory_length
        self._importance_threshold = importance_threshold
        self._memory = None
        self._summary_option = summary_option

    def get_importance(self, json_input):
        """
        Takes a json_input encoding a batch of evaluated individuals together with their objective values. Returns a
        dictionary mapping the encoding variables onto their importance value (individuals seen earlier may be
        considered during the importance calculation).

        :param json_input: the json input encoding a batch of evaluated individuals
        :return:  a dictionary mapping the encoding variables onto their importance value, the info log string of the
                  operation
        """
        start_read = time.time()
        df = self._json_to_data_frame(json.dumps(json_input))
        # drop any rows with nans
        df = df.dropna()
        if df.empty:
            return json.dumps({}), "empty df"
        time_read = time.time() - start_read
        column_num = df.shape[1]
        row_num = df.shape[0]
        # get the data used for fitting
        information_df = self._consider_memory(df)
        # do the fitting
        result_dict, log_fitting = self._calculate_importance(information_df)
        row_num_overall = information_df.shape[0]
        column_num_overall = information_df.shape[1]
        important_var_num = len(result_dict[variable_id_out_string])
        time_overall = time.time() - start_read
        log_string = self._generate_log_string(time_read, row_num, column_num, log_fitting, row_num_overall,
                                               column_num_overall, important_var_num, time_overall)
        return json.dumps(result_dict), log_string

    @staticmethod
    def _generate_log_string(time_read, row_num, column_num, log_fitting, row_num_overall,
                             column_num_overall,important_var_num, time_overall):
        """
        Generates the string used for logging.

        :param time_read the time for reading
        :param column_num the number of the columns in the df
        :param row_num the number of the rows in the df
        :param log_fitting the log string returned by the fitting function
        :param row_num_overall overall number of rows used for learning
        :param column_num_overall overall number of features + labels
        :param important_var_num number of important variables
        :param time_overall the overall time required by the Python part
        :return: the log string which is used as info log
        """
        info_string = ''
        info_string += ("Reading took {}".format(time_read))
        info_string += '\n'
        info_string += ("Got a df with {} lines and {} columns".format(row_num, column_num))
        info_string += '\n'
        info_string += log_fitting
        info_string += '\n'
        info_string += ("Overall working with {} lines and {} columns".format(row_num_overall, column_num_overall))
        info_string += '\n'
        info_string += ("Extracted {} importance variables.".format(important_var_num))
        info_string += '\n'
        info_string += ("The whole python part took {}".format(time_overall))
        return info_string

    def _consider_memory(self, df):
        """
        Takes the current batch of individuals and returns a df that considers the previously seen individuals according
        to the memory parameters of the current class instance

        :param df: the data frame containing the evaluated indis of the current batch
        :return: a data frame of evaluated individuals that considers the memory
        """
        if not self._has_memory:
            # no memory => just return the current batch
            return df
        else:
            # memory => adjust the memory and return it
            if self._memory is None:
                # first access
                self._memory = df
            else:
                # add the new frame to the memory
                self._memory = self._memory.append(df, ignore_index=True)
            return self._memory

    def _calculate_importance(self, df):
        """
        Calculate the importance of the SAT variables based on a data frame of evaluated individuals.

        :param df: string containing the assignments of ALL variables of the model as well as the objective
        values
        :return: json string containing (id, importance) pairs for the important variables, log_string
        """
        feature_columns, objective_columns = self._divide_objectives_from_features(df)
        start_ml = time.time()
        # iterate the objective functions and get the importance measures for each of them
        importance_per_objective = []
        for objective in objective_columns:
            importance_per_objective.append(self._calculate_importance_for_objective(df, objective, feature_columns))
        log_string = ("The fitting took {}".format(time.time() - start_ml))
        # make a dict with the average
        importance_dict = self._summarize_variable_importance(importance_per_objective)
        # process the dict into the result
        return self._make_result_dictionary(importance_dict), log_string

    @staticmethod
    def _make_result_dictionary(importance_dictionary):
        """
        Takes a dictionary mapping the variable ids onto their importance values and returns a dictionary that can be
        turned into the json expected as result by the server.

        :param importance_dictionary: a dictionary mapping the variable ids onto their importance values
        :return: a dictionary that can be turned into the json expected as result by the server
        """
        id_list = []
        importance_list = []
        for feature_id, importance in importance_dictionary.iteritems():
            id_list.append(feature_id)
            importance_list.append(importance)
        return {variable_id_out_string: id_list, importance_out_string: importance_list}

    def _summarize_variable_importance(self, result_dict_list):
        """
        Takes the importance dictionaries (relating to a single objective each) and calculates the average importance of
        the variable contained therein. Returns a dictionary describing the overall importance with respect to all
        objectives.

        :param result_dict_list: a list with the dictionaries for the individual objectives
        :return: a dictionary containing the average feature importance over all objectives
        """
        objective_number = len(result_dict_list)
        result = {}
        # gather all variables
        variable_list = []
        for result_dict in result_dict_list:
            for variable in result_dict:
                variable_list.append(variable)
        # iterate all variables and calculate the average importance
        for variable in variable_list:
            importance_list = []
            for result_dict in result_dict_list:
                if variable in result_dict:
                    importance_list.append(result_dict[variable])
                else:
                    importance_list.append(0.0)
            importance_summary = self._summarize_objective_importance(importance_list)
            if importance_summary >= self._importance_threshold:
                result[variable] = importance_summary
        return result

    def _summarize_objective_importance(self, importance_list):
        """
        Gets a list of importance values w.r.t. different objectives and returns a value summarizing them

        :param importance_list: list of importance values w.r.t. the optimization objectives
        :return: summary value
        """
        if self._summary_option == 'avg':
            importance_sum = 0
            for importance in importance_list:
                importance_sum += importance
            return importance_sum / len(importance_list)
        if self._summary_option == 'max':
            return max(importance_list)

    @staticmethod
    def _calculate_importance_for_objective(df, objective_name, feature_names):
        """
        Calculates the importance of the variables with respect to ONE objective.

        :param df: the df read from the request json
        :param objective_name: the name of the currently considered objective
        :param feature_names: the names of the features
        :return: a dict mapping the variables (feature names) to their individual importance variables
        """
        feature_df = df[feature_names]
        labels = df[objective_name]
        result = {}
        clf = DecisionTreeRegressor()
        clf.fit(feature_df, labels)
        importance_list = clf.feature_importances_
        # iterate the importance list and make the result dictionary
        for idx, importance in enumerate(importance_list):
            feature_name = feature_names[idx]
            result[feature_name] = importance
        return result

    @staticmethod
    def _json_to_data_frame(json_input):
        """
        Takes the json provided as input and turns it into a data frame.

        :param json_input: the input json
        :return: data_frame
        """
        return pd.read_json(json_input, orient='split')

    @staticmethod
    def _divide_objectives_from_features(df):
        """
        Takes the data frame made from the json and returns two lists, where the first contains only the feature names and
        the second contains only the objective names.

        :param df: the data frame obtained by reading the json input
        :return: (feature_columns, objective_columns)
        """
        # make a list of the feature columns
        feature_columns = df.columns.tolist()
        objective_columns = []
        # find the objective columns
        for column in feature_columns:
            if column.split(objective_separator_in)[0] == objective_in_string:
                objective_columns.append(column)

        for objective_column in objective_columns:
            feature_columns.remove(objective_column)
        return feature_columns, objective_columns
