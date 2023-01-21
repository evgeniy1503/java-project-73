// @ts-check

import path from 'path';

// const hostname = 'localhost';
// const port = process.env.REACT_APP_PORT || 5001;
const apiUrl = '/api';
const { host, protocol } = window.location;
const fullHost = `${protocol}//${host}`;

const buildUrl = (part) => () => {
  const urlPath = path.join(apiUrl, part);
  const url = new URL(urlPath, fullHost);
  return url.toString();
};

const buildLocalUrl = (part) => () => `/${part}`;

const routes = {
  homePagePath: buildLocalUrl(''),
  loginPagePath: buildLocalUrl('login'),
  signupPagePath: buildLocalUrl('signup'),
  usersPagePath: buildLocalUrl('users'),
  userEditPagePath: (id) => `${buildLocalUrl('users')()}/${id}/edit`,

  statusesPagePath: buildLocalUrl('statuses'),
  statusPagePath: (id) => `${buildLocalUrl('statuses')()}/${id}`,
  newStatusPagePath: () => `${buildLocalUrl('statuses')()}/new`,
  statusEditPagePath: (id) => `${buildLocalUrl('statuses')()}/${id}/edit`,

  labelsPagePath: buildLocalUrl('labels'),
  labelPagePath: (id) => `${buildLocalUrl('labels')()}/${id}`,
  newLabelPagePath: () => `${buildLocalUrl('labels')()}/new`,
  labelEditPagePath: (id) => `${buildLocalUrl('labels')()}/${id}/edit`,

  tasksPagePath: buildLocalUrl('tasks'),
  taskPagePath: (id) => `${buildLocalUrl('tasks')()}/${id}`,
  newTaskPagePath: () => `${buildLocalUrl('tasks')()}/new`,
  taskEditPagePath: (id) => `${buildLocalUrl('tasks')()}/${id}/edit`,

  apiTasks: buildUrl('tasks'),
  apiTask: (id) => `${buildUrl('tasks')()}/${id}`,
  apiLabels: buildUrl('labels'),
  apiLabel: (id) => `${buildUrl('labels')()}/${id}`,
  apiStatuses: buildUrl('statuses'),
  apiStatus: (id) => `${buildUrl('statuses')()}/${id}`,
  apiUsers: buildUrl('users'),
  apiUser: (id) => `${buildUrl('users')()}/${id}`,
  apiLogin: buildUrl('login'),
};

export default routes;
