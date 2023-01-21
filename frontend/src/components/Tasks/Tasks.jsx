// @ts-check

import React, { useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { useTranslation } from 'react-i18next';
import { Table, Form, Button } from 'react-bootstrap';
import axios from 'axios';
import { Link, useHistory } from 'react-router-dom';

import handleError from '../../utils.js';
import { useAuth, useNotify } from '../../hooks/index.js';
import routes from '../../routes.js';
import TaskFilter from './TaskFilter.jsx';
import { actions as taskActions, selectors as tasksSelectors } from '../../slices/tasksSlice.js';

import { selectors as userSelectors } from '../../slices/usersSlice.js';
import { selectors as taskStatuseSelectors } from '../../slices/taskStatusesSlice.js';

const Tasks = () => {
  const { t } = useTranslation();
  // const tasks = useSelector(tasksSelectors.selectAll);
  const [filteredTasks, setFilteredTasks] = useState(null);
  const auth = useAuth();
  const notify = useNotify();
  const history = useHistory();
  const dispatch = useDispatch();

  const { executors, taskStatuses, tasks } = useSelector((state) => ({
    executors: userSelectors.selectAll(state),
    taskStatuses: taskStatuseSelectors.selectAll(state),
    tasks: tasksSelectors.selectAll(state),
  }));

  if (!tasks) {
    return null;
  }

  const removeTask = async (event, id) => {
    event.preventDefault();
    try {
      await axios.delete(routes.apiTask(id), { headers: auth.getAuthHeader() });
      dispatch(taskActions.removeTask((id)));
      notify.addMessage('taskRemoved');
    } catch (e) {
      if (e.response?.status === 403) {
        notify.addErrors([{ text: t('taskEditDenied') }]);
      } else if (e.response?.status === 422) {
        notify.addError('taskRemoveFail');
      } else {
        handleError(e, notify, history, auth);
      }
    }
  };

  return (
    <>
      <Link to={routes.newTaskPagePath()}>{t('createTask')}</Link>
      <TaskFilter foundTasks={setFilteredTasks} />
      <Table striped hover>
        <thead>
          <tr>
            <th>{t('id')}</th>
            <th>{t('naming')}</th>
            <th>{t('status')}</th>
            <th>{t('author')}</th>
            <th>{t('executor')}</th>
            <th>{t('createDate')}</th>
            <th>{null}</th>
          </tr>
        </thead>
        <tbody>
          {(filteredTasks ?? tasks).map((task) => {
            const executor = task.executor ? executors
              .find((item) => item.id.toString() === task.executor.id.toString()) : null;
            const author = executors
              .find((item) => item.id.toString() === task.author.id.toString());
            const taskStatus = taskStatuses
              .find((item) => item.id.toString() === task.taskStatus.id.toString());
            return (
              <tr key={task.id}>
                <td>{task.id}</td>
                <td>
                  <Link to={routes.taskPagePath(task.id)}>{task.name}</Link>
                </td>
                <td>{taskStatus.name}</td>
                <td>{`${author.firstName} ${author.lastName}`}</td>
                <td>{executor ? `${executor.firstName} ${executor.lastName}` : ''}</td>
                <td>{new Date(task.createdAt).toLocaleString('ru')}</td>
                <td>
                  <Link to={routes.taskEditPagePath(task.id)}>{t('edit')}</Link>
                  <Form onSubmit={(event) => removeTask(event, task.id)}>
                    <Button type="submit" variant="link">{t('remove')}</Button>
                  </Form>
                </td>
              </tr>
            );
          })}
        </tbody>
      </Table>
    </>
  );
};

export default Tasks;
