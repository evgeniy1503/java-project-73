// @ts-check

import { configureStore } from '@reduxjs/toolkit';
import usersReducer from './usersSlice.js';
import labelsReducer from './labelsSlice.js';
import taskStatusesReducer from './taskStatusesSlice.js';
import tasksReducer from './tasksSlice.js';
import notifyReducer from './notificationSlice.js';

export default configureStore({
  reducer: {
    users: usersReducer,
    labels: labelsReducer,
    taskStatuses: taskStatusesReducer,
    tasks: tasksReducer,
    notify: notifyReducer,
  },
});
