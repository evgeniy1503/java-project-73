import _ from 'lodash';

import getUsers from '../__fixtures__/executors.js';
import getLabels from '../__fixtures__/labels.js';
import getStatuses from '../__fixtures__/statuses.js';
import getTasks from '../__fixtures__/tasks.js';

const mockInitialData = (_req, res, ctx) => {
  const data = {
    channels: [{ id: 1, name: 'General' }, { id: 2, name: 'Random' }],
    messages: [],
    currentChannelId: 1,
  };

  return res(
    ctx.status(200),
    ctx.json(data),
  );
};

const mockSignup = (_req, res, ctx) => res(
  ctx.status(200),
  ctx.json({ token: 'token' }),
);

const mockSingin = (_req, res, ctx) => res(
  ctx.status(201),
  ctx.json({ token: 'token' }),
);

const mockServer = (server, rest) => {
  const tasks = getTasks();
  const users = getUsers();
  const labels = getLabels();
  const taskStatuses = getStatuses();

  server.use(
    rest.post('/api/login', mockSingin),

    rest.post('/api/statuses', (_req, res, ctx) => {
      const result = {
        ..._req.body,
        id: _.uniqueId('test_'),
        createdAt: Date.now(),
      };
      return res(ctx.status(200), ctx.json(result));
    }),
    rest.get('/api/statuses', (_req, res, ctx) => {
      const result = taskStatuses;
      return res(ctx.status(200), ctx.json(result));
    }),

    rest.get('/api/statuses/:taskStatusId', (_req, res, ctx) => {
      const { taskStatusId } = _req.params;
      const result = taskStatuses.find((status) => status.id.toString() === taskStatusId);
      return res(ctx.status(200), ctx.json(result));
    }),

    rest.put('/api/statuses/:taskStatusId', (_req, res, ctx) => {
      const { taskStatusId } = _req.params;
      const currentItem = taskStatuses.find((status) => status.id.toString() === taskStatusId);
      const result = {
        ...currentItem,
        ..._req.body,
      };
      return res(ctx.status(200), ctx.json(result));
    }),
    rest.delete('/api/statuses/:taskStatusId', (_req, res, ctx) => res(ctx.status(200))),

    rest.post('/api/labels', (_req, res, ctx) => {
      const result = {
        ..._req.body,
        id: _.uniqueId('test_'),
        createdAt: Date.now(),
      };
      return res(ctx.status(200), ctx.json(result));
    }),
    rest.get('/api/labels', (_req, res, ctx) => {
      const result = labels;
      return res(ctx.status(200), ctx.json(result));
    }),
    rest.get('/api/labels/:taskLabelId', (_req, res, ctx) => {
      const { taskLabelId } = _req.params;
      const result = labels.find((label) => label.id.toString() === taskLabelId);
      return res(ctx.status(200), ctx.json(result));
    }),
    rest.put('/api/labels/:taskLabelId', (_req, res, ctx) => {
      const { taskLabelId } = _req.params;
      const currentItem = labels.find((label) => label.id.toString() === taskLabelId);
      const result = {
        ...currentItem,
        ..._req.body,
      };
      return res(ctx.status(200), ctx.json(result));
    }),
    rest.delete('/api/labels/:id', (_req, res, ctx) => res(ctx.status(200))),

    rest.post('/api/tasks', (_req, res, ctx) => {
      const result = {
        ..._req.body,
        id: _.uniqueId('test_'),
        createdAt: Date.now(),
        author: {
          id: users[0].id,
        },
        taskStatus: {
          id: _req.body.taskStatusId,
        },
      };
      return res(ctx.status(200), ctx.json(result));
    }),
    rest.get('/api/tasks', (_req, res, ctx) => {
      const result = tasks;
      return res(ctx.status(200), ctx.json(result));
    }),
    rest.get('/api/tasks/:taskId', (_req, res, ctx) => {
      const { taskId } = _req.params;
      const result = tasks.find((task) => task.id.toString() === taskId);
      return res(ctx.status(200), ctx.json(result));
    }),
    rest.put('/api/tasks/:taskId', (_req, res, ctx) => {
      const { taskId } = _req.params;
      const currentItem = tasks.find((task) => task.id.toString() === taskId);
      const result = {
        ...currentItem,
        ..._req.body,
        taskStatus: {
          check: 'hello',
          id: _req.body.taskStatusId,
        },
      };
      return res(ctx.status(200), ctx.json(result));
    }),
    rest.delete('/api/tasks/:id', (_req, res, ctx) => {
      const result = {
        ..._req.body,
      };
      return res(ctx.status(200), ctx.json(result));
    }),

    rest.post('/api/users', (_req, res, ctx) => {
      const result = {
        ..._req.body,
        id: _.uniqueId('test_'),
        createdAt: Date.now(),
      };
      return res(ctx.status(200), ctx.json(result));
    }),
    rest.get('/api/users', (_req, res, ctx) => {
      const result = users;
      return res(ctx.status(200), ctx.json(result));
    }),
    rest.get('/api/users/:userId', (_req, res, ctx) => {
      const { userId } = _req.params;
      const result = users.find((u) => u.id.toString() === userId);
      return res(ctx.status(200), ctx.json(result));
    }),
    rest.put('/api/users/:userId', (_req, res, ctx) => {
      const { userId } = _req.params;
      const currentItem = users.find((u) => u.id.toString() === userId);
      const result = {
        ...currentItem,
        ..._req.body,
      };
      return res(ctx.status(200), ctx.json(result));
    }),
    rest.delete('/api/users/:id', (_req, res, ctx) => res(ctx.status(200))),
  );
  server.listen({
    onUnhandledRequest: (req) => {
      console.error(`There is no handler for "${req.url.href}"`);
    },
  });
};

export default {
  mockInitialData,
  mockSignup,
  mockSingin,
  mockServer,
};
