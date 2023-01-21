// @ts-check

import React, { useEffect, useState } from 'react';
import {
  Switch,
  Route,
  useHistory,
} from 'react-router-dom';
import { useDispatch } from 'react-redux';
import axios from 'axios';

import Notification from './Notification.jsx';

import Navbar from './Navbar.jsx';
import Welcome from './Welcome.jsx';
import Login from './Login.jsx';
import Registration from './Registration.jsx';
import NotFoundPage from './NotFoundPage.jsx';
import UsersComponent from './Users/Users.jsx';
import EditUser from './Users/EditUser.jsx';

import Statuses from './Statuses/Statuses.jsx';
import EditStatus from './Statuses/EditStatus.jsx';
import NewStatus from './Statuses/NewStatus.jsx';

import Labels from './Labels/Labels.jsx';
import EditLabel from './Labels/EditLabel.jsx';
import NewLabel from './Labels/NewLabel.jsx';

import Task from './Tasks/Task.jsx';
import Tasks from './Tasks/Tasks.jsx';
import NewTask from './Tasks/NewTask.jsx';
import EditTask from './Tasks/EditTask.jsx';

import routes from '../routes.js';

import { actions as usersActions } from '../slices/usersSlice.js';
import { actions as labelsActions } from '../slices/labelsSlice.js';
import { actions as taskStatusesActions } from '../slices/taskStatusesSlice.js';
import { actions as tasksActions } from '../slices/tasksSlice.js';

import { useNotify, useAuth } from '../hooks/index.js';
import handleError from '../utils.js';

import getLogger from '../lib/logger.js';

const log = getLogger('App');
log.enabled = true;

const App = () => {
  const notify = useNotify();
  const history = useHistory();
  const auth = useAuth();
  const dispatch = useDispatch();
  const [isLoading, setLoading] = useState(true);

  useEffect(() => {
    setLoading(true);
    const dataRoutes = [
      {
        name: 'users',
        getData: async () => {
          const { data } = await axios.get(routes.apiUsers());
          if (!Array.isArray(data)) {
            notify.addError('Сервер не вернул список пользователей');
            dispatch(usersActions.addUsers([]));
            return;
          }
          dispatch(usersActions.addUsers(data));
        },
        isSecurity: false,
      },
      {
        name: 'labels',
        getData: async () => {
          const { data } = await axios.get(routes.apiLabels(), { headers: auth.getAuthHeader() });
          if (!Array.isArray(data)) {
            notify.addError('Сервер не вернул список меток');
            dispatch(labelsActions.addLabels([]));
            return;
          }
          dispatch(labelsActions.addLabels(data));
        },
        isSecurity: true,
      },
      {
        name: 'taskStatuses',
        getData: async () => {
          const { data } = await axios
            .get(routes.apiStatuses(), { headers: auth.getAuthHeader() });
          if (!Array.isArray(data)) {
            notify.addError('Сервер не вернул список статусов');
            dispatch(taskStatusesActions.addTaskStatuses([]));
            return;
          }
          dispatch(taskStatusesActions.addTaskStatuses(data));
        },
        isSecurity: true,
      },
      {
        name: 'tasks',
        getData: async () => {
          const { data } = await axios.get(routes.apiTasks(), { headers: auth.getAuthHeader() });
          if (!Array.isArray(data)) {
            notify.addError('Сервер не вернул список задач');
            dispatch(tasksActions.addTasks([]));
            return;
          }
          dispatch(tasksActions.addTasks(data));
        },
        isSecurity: true,
      },
    ];
    const promises = dataRoutes.filter(({ isSecurity }) => (isSecurity ? auth.user : true))
      .map(({ getData }) => getData());
    Promise.all(promises)
      .catch((error) => handleError(error, notify, history, auth))
      .finally(() => setLoading(false));
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [auth.user]);

  const PrivateRoute = ({ children }) => {
    if (!auth.user) {
      const from = { pathname: routes.homePagePath() };
      history.push(from, { message: 'accessDenied', type: 'error' });
      return null;
    }
    return children;
  };

  if (isLoading) {
    return null;
  }

  return (
    <>
      <Navbar />
      <div className="container wrapper flex-grow-1">
        <Notification />
        <h1 className="my-4">{null}</h1>
        <Switch>
          <Route exact path={routes.homePagePath()} component={Welcome} />
          <Route path={routes.loginPagePath()} component={Login} />
          <Route path={routes.signupPagePath()} component={Registration} />

          <Route exact path={routes.usersPagePath()}><UsersComponent /></Route>
          <Route path={routes.userEditPagePath(':userId')}>
            <PrivateRoute><EditUser /></PrivateRoute>
          </Route>

          <Route exact path={routes.statusesPagePath()}>
            <PrivateRoute><Statuses /></PrivateRoute>
          </Route>
          <Route path={routes.newStatusPagePath()}>
            <PrivateRoute><NewStatus /></PrivateRoute>
          </Route>
          <Route path={routes.statusEditPagePath(':taskStatusId')}>
            <PrivateRoute><EditStatus /></PrivateRoute>
          </Route>

          <Route exact path={routes.labelsPagePath()}>
            <PrivateRoute><Labels /></PrivateRoute>
          </Route>
          <Route path={routes.labelEditPagePath(':labelId')}>
            <PrivateRoute><EditLabel /></PrivateRoute>
          </Route>
          <Route path={routes.newLabelPagePath()}>
            <PrivateRoute><NewLabel /></PrivateRoute>
          </Route>

          <Route exact path={routes.tasksPagePath()}>
            <PrivateRoute><Tasks /></PrivateRoute>
          </Route>
          <Route path={routes.newTaskPagePath()}>
            <PrivateRoute><NewTask /></PrivateRoute>
          </Route>
          <Route path={routes.taskEditPagePath(':taskId')}>
            <PrivateRoute><EditTask /></PrivateRoute>
          </Route>
          <Route path={routes.taskPagePath(':taskId')}>
            <PrivateRoute><Task /></PrivateRoute>
          </Route>

          <Route path="*" component={NotFoundPage} />
        </Switch>
      </div>
      <footer>
        <div className="container my-5 pt-4 border-top">
          <a rel="noreferrer" href="https://ru.hexlet.io">Hexlet</a>
        </div>
      </footer>
    </>
  );
};

export default App;
