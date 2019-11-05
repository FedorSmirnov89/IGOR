"""
This script is run as a python app to activate the server that is responsible for the machine learning part of the
dynamic variable order adjustment approach.

"""
from flask import Flask, request
from content.calc.importance_calculation import VariableImportanceCalculator
import logging
from logging.handlers import RotatingFileHandler
import json
import datetime

app = Flask(__name__)
server_state = {}


@app.route('/initialize_optimization_run', methods=['POST'])
def initialize_optimization_run():
    """
    Initializes the optimization run by creating the object used to calculate the importance.
    :return: A json string confirming a successful initialization
    """
    print ('Initialization request received.')
    json_data = request.get_json()
    parameters = json_data['commandParameters']
    has_memory = parameters['importance_memory']
    importance_threshold = parameters['importance_threshold']
    summary_method = parameters['importance_summary']
    print(summary_method)
    server_state['calculator'] = VariableImportanceCalculator(has_memory=has_memory,
                                                              importance_threshold=importance_threshold,
                                                              summary_option=summary_method)
    response = {'success': True}
    print (response)
    return json.dumps(response)


@app.route('/shut_down_server', methods=['POST', 'GET'])
def shutdown_socket():
    """
    Shuts down the server (called at the end of the optimization)
    :return: Nothing
    """
    func = request.environ.get('werkzeug.server.shutdown')
    if func is None:
        raise RuntimeError('Not running with the Werkzeug Server')
    print ('Optimization finished. Shutting down the server.')
    func()
    return 'empty'


@app.route('/calculate_importance', methods=['POST'])
def calculate_variable_importance():
    """
    Gets a batch of individuals (assignments of ALL variables and the objective functions) and calculates the importance
    of the variables, i.e., their influence on the membership in a similarity group shared with other individuals
    :return: A json string containing the importances of the most important variables
    """
    app.logger.info('Importance calculation request received')
    json_input = request.get_json()
    importance_calculator = server_state['calculator']
    # do stuff
    response, log_string = importance_calculator.get_importance(json_input)
    app.logger.info(log_string)
    app.logger.info(response)
    app.logger.info("")
    return response


if __name__ == '__main__':
    # initialize the log handler
    cur_time = datetime.datetime.now()
    log_info_file = 'info_run_{}.log'.format(cur_time)
    log_error_file = 'error_run_{}.log'.format(cur_time)
    logHandler_info = RotatingFileHandler(log_info_file, maxBytes=1000, backupCount=0)
    logHandler_error = RotatingFileHandler(log_error_file, maxBytes=1000, backupCount=0)
    # set the log handler level
    logHandler_error.setLevel(logging.ERROR)
    logHandler_info.setLevel(logging.INFO)
    # set the app logger level
    app.logger.setLevel(logging.ERROR)
    app.logger.setLevel(logging.INFO)
    app.logger.addHandler(logHandler_info)
    app.logger.addHandler(logHandler_error)
    app.run()
