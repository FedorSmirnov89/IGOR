"""
Test methods for the importance_calculation module.
"""

import unittest
from content.calc.importance_calculation import VariableImportanceCalculator

expected_df_string_file = 'tests/Expected_df_read_string.txt'
string_json_input = '{"columns":["feature 1","feature 2","feature 3","Objective Function:0","Objective Function:1"],' \
                    '"index":["1","2","3"],"data":[[1.0,0.0,0.0,100.0,10.0],[0.0,0.0,0.0,50.0,10.0],' \
                    '[1.0,1.0,1.0,150.0,5.0]]}'

string_json_addition = '{"columns":["feature 1","feature 2","feature 3","Objective Function:0","Objective Function:1"],' \
                    '"index":["1"],"data":[[0.0,0.0,0.0,100.0,0.0]]}'

string_json_merged = '{"columns":["feature 1","feature 2","feature 3","Objective Function:0","Objective Function:1"],' \
                    '"index":["0","1","2","3"],"data":[[1.0,0.0,0.0,100.0,10.0],[0.0,0.0,0.0,50.0,10.0],' \
                    '[1.0,1.0,1.0,150.0,5.0],[0.0,0.0,0.0,100.0,0.0]]}'

expected_feature_result = [u'feature 1', u'feature 2', u'feature 3']
expected_obj_result = [u'Objective Function:0', u'Objective Function:1']


class TestImportanceCalculation(unittest.TestCase):

    def test_consider_memory(self):
        # case without memory
        tested = VariableImportanceCalculator()
        df = tested._json_to_data_frame(string_json_input)
        result_df = tested._consider_memory(df)
        self.assertEqual(result_df.to_string(), df.to_string())
        # case with memory
        tested_2 = VariableImportanceCalculator(has_memory=True)
        # first run => returns input
        df_2 = tested_2._json_to_data_frame(string_json_input)
        result_df = tested_2._consider_memory(df)
        self.assertEqual(result_df.to_string(), df_2.to_string())
        self.assertEqual(df_2.to_string(), tested_2._memory.to_string())
        # second run => returns merged df
        df_3 = tested_2._json_to_data_frame(string_json_addition)
        result_df = tested_2._consider_memory(df_3)
        expected = tested_2._json_to_data_frame(string_json_merged)
        self.assertEqual(expected.to_string(), result_df.to_string())

    def test_json_to_data_frame(self):
        tested = VariableImportanceCalculator()
        df = tested._json_to_data_frame(string_json_input)
        result_string = df.to_string()
        with open(expected_df_string_file, 'r') as my_file:
            expected = my_file.read()
        self.assertEqual(expected, result_string)

    def test_divide_objectives_from_features(self):
        tested = VariableImportanceCalculator()
        df = tested._json_to_data_frame(string_json_input)
        feature_result, objective_result = tested._divide_objectives_from_features(df)
        self.assertEqual(expected_feature_result, feature_result)
        self.assertEqual(expected_obj_result, objective_result)

    def test_calculate_importance(self):
        tested = VariableImportanceCalculator()
        df = tested._json_to_data_frame(string_json_input)
        feature_result, objective_result = tested._divide_objectives_from_features(df)
        result0 = tested._calculate_importance_for_objective(df, 'Objective Function:0', feature_result)
        result1 = tested._calculate_importance_for_objective(df, 'Objective Function:1', feature_result)
        self.assertGreater(len(result0), 1)
        self.assertGreater(len(result1), 0)

    def test_summarize_importance_average(self):
        tested = VariableImportanceCalculator()
        dict_1 = {'a': .6, 'b': .4}
        dict_2 = {'a': .2, 'c': .8}
        dict_list = [dict_1, dict_2]
        result_dict = tested._summarize_variable_importance(dict_list)
        self.assertEqual(.4, result_dict['a'])
        self.assertEqual(.2, result_dict['b'])
        self.assertEqual(.4, result_dict['c'])

    def test_make_result_dictionary(self):
        tested = VariableImportanceCalculator()
        importance_dict = {'a': .5, 'b': .2, 'c': 0.0}
        result = tested._make_result_dictionary(importance_dict)
        ids = result['variableIds']
        importance = result['importanceValues']
        self.assertEqual(.5, importance[ids.index('a')])
        self.assertEqual(.2, importance[ids.index('b')])
        self.assertEqual(0.0, importance[ids.index('c')])


if __name__ == '__main__':
    unittest.main()
