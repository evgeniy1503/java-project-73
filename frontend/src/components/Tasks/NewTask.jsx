// @ts-check

import React from 'react';
import { useHistory } from 'react-router-dom';
import { useSelector, useDispatch } from 'react-redux';
import { useTranslation } from 'react-i18next';
import { Form, Button } from 'react-bootstrap';
import { useFormik } from 'formik';
import * as yup from 'yup';
import axios from 'axios';

import { actions as tasksActions } from '../../slices/tasksSlice.js';
import routes from '../../routes.js';
import { useAuth, useNotify } from '../../hooks/index.js';
import handleError from '../../utils.js';
import { selectors as userSelectors } from '../../slices/usersSlice.js';
import { selectors as labelSelectors } from '../../slices/labelsSlice.js';
import { selectors as taskStatuseSelectors } from '../../slices/taskStatusesSlice.js';

import getLogger from '../../lib/logger.js';

const log = getLogger('client');
log.enabled = true;

const getValidationSchema = () => yup.object().shape({});

const NewTask = () => {
  const { t } = useTranslation();
  const dispatch = useDispatch();

  const history = useHistory();
  const { executors, labels, taskStatuses } = useSelector((state) => ({
    executors: userSelectors.selectAll(state),
    labels: labelSelectors.selectAll(state),
    taskStatuses: taskStatuseSelectors.selectAll(state),
  }));

  const auth = useAuth();
  const notify = useNotify();

  const f = useFormik({
    initialValues: {
      name: '',
      description: '',
      taskStatusId: null,
      executorId: null,
      labelIds: [],
    },
    validationSchema: getValidationSchema(),
    onSubmit: async (taskData, { setSubmitting, setErrors }) => {
      try {
        const requestTask = {
          name: taskData.name,
          description: taskData.description,
          executorId: parseInt(taskData.executorId, 10),
          taskStatusId: parseInt(taskData.taskStatusId, 10),
          labelIds: taskData.labelIds.map((id) => parseInt(id, 10)),
        };
        const { data } = await axios
          .post(routes.apiTasks(), requestTask, { headers: auth.getAuthHeader() });
        // data.taskStatus = taskStatuses.find((item) => item.id === data.taskStatus.id);
        log('task.create', data);
        dispatch(tasksActions.addTask(data));
        const from = { pathname: routes.tasksPagePath() };
        history.push(from, { message: 'taskCreated' });
      } catch (e) {
        log('task.create.error', e);
        setSubmitting(false);
        if (e.response?.status === 422 && Array.isArray(e.response?.data)) {
          const errors = e.response.data
            .reduce((acc, err) => ({ ...acc, [err.field]: err.defaultMessage }), {});
          setErrors(errors);
          notify.addError('taskCreateFail');
        } else {
          handleError(e, notify, history, auth);
        }
      }
    },
    validateOnBlur: false,
    validateOnChange: false,
  });

  return (
    <>
      <h1 className="my-4">{t('taskCreating')}</h1>
      <Form onSubmit={f.handleSubmit}>
        <Form.Group className="mb-3">
          <Form.Label htmlFor="name">{t('naming')}</Form.Label>
          <Form.Control
            type="text"
            value={f.values.name}
            disabled={f.isSubmitting}
            onChange={f.handleChange}
            onBlur={f.handleBlur}
            isInvalid={f.errors.name && f.touched.name}
            id="name"
            name="name"
          />
          <Form.Control.Feedback type="invalid">
            {t(f.errors.name)}
          </Form.Control.Feedback>
        </Form.Group>

        <Form.Group className="mb-3">
          <Form.Label htmlFor="description">{t('description')}</Form.Label>
          <Form.Control
            as="textarea"
            rows={3}
            value={f.values.description}
            disabled={f.isSubmitting}
            onChange={f.handleChange}
            onBlur={f.handleBlur}
            isInvalid={f.errors.description && f.touched.description}
            id="description"
            name="description"
          />
          <Form.Control.Feedback type="invalid">
            {t(f.errors.description)}
          </Form.Control.Feedback>
        </Form.Group>

        <Form.Group className="mb-3">
          <Form.Label htmlFor="taskStatusId">{t('status')}</Form.Label>
          <Form.Select
            nullable
            value={f.values.taskStatusId}
            disabled={f.isSubmitting}
            onChange={f.handleChange}
            onBlur={f.handleBlur}
            isInvalid={f.errors.taskStatusId && f.touched.taskStatusId}
            id="taskStatusId"
            name="taskStatusId"
          >
            <option value="">{null}</option>
            {taskStatuses
              .map((status) => <option key={status.id} value={status.id}>{status.name}</option>)}
          </Form.Select>
          <Form.Control.Feedback type="invalid">
            {t(f.errors.taskStatusId)}
          </Form.Control.Feedback>
        </Form.Group>

        <Form.Group className="mb-3">
          <Form.Label htmlFor="executorId">{t('executor')}</Form.Label>
          <Form.Select
            multiple
            value={f.values.executorId}
            disabled={f.isSubmitting}
            onChange={f.handleChange}
            onBlur={f.handleBlur}
            isInvalid={f.errors.executorId && f.touched.executorId}
            id="executorId"
            name="executorId"
          >
            <option value="">{null}</option>
            {executors.map((executor) => <option key={executor.id} value={executor.id}>{`${executor.firstName} ${executor.lastName}`}</option>)}
          </Form.Select>
          <Form.Control.Feedback type="invalid">
            {t(f.errors.executorId)}
          </Form.Control.Feedback>
        </Form.Group>

        <Form.Group className="mb-3">
          <Form.Label htmlFor="labelIds">{t('labels')}</Form.Label>
          <Form.Select
            multiple
            value={f.values.labelIds}
            disabled={f.isSubmitting}
            onChange={f.handleChange}
            onBlur={f.handleBlur}
            isInvalid={f.errors.labelIds && f.touched.labelIds}
            id="labelIds"
            name="labelIds"
          >
            {labels.map((label) => <option key={label.id} value={label.id}>{label.name}</option>)}
          </Form.Select>
          <Form.Control.Feedback type="invalid">
            {t(f.errors.labelIds)}
          </Form.Control.Feedback>
        </Form.Group>

        <Button variant="primary" type="submit">
          {t('create')}
        </Button>
      </Form>
    </>
  );
};

export default NewTask;
