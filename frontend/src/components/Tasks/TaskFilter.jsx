// @ts-check

import React from 'react';
import { useSelector } from 'react-redux';
import axios from 'axios';
import { useFormik } from 'formik';
import { useTranslation } from 'react-i18next';
import { useHistory } from 'react-router-dom';
import {
  Card, Button, Col, Form, Row,
} from 'react-bootstrap';

import handleError from '../../utils.js';
import { useAuth, useNotify } from '../../hooks/index.js';
import routes from '../../routes.js';
import { selectors as userSelectors } from '../../slices/usersSlice.js';
import { selectors as labelSelectors } from '../../slices/labelsSlice.js';
import { selectors as taskStatuseSelectors } from '../../slices/taskStatusesSlice.js';

const TaskFilter = (props) => {
  const { foundTasks: handler } = props;
  const auth = useAuth();
  const history = useHistory();
  const { t } = useTranslation();
  const notify = useNotify();
  const { executors, labels, taskStatuses } = useSelector((state) => ({
    executors: userSelectors.selectAll(state),
    labels: labelSelectors.selectAll(state),
    taskStatuses: taskStatuseSelectors.selectAll(state),
  }));

  const f = useFormik({
    initialValues: {
      taskStatusId: null,
      executorId: null,
      labelId: null,
      isMyTasks: false,
    },
    onSubmit: async (formData, { setSubmitting }) => {
      notify.clean();
      try {
        const params = {};
        if (formData.isMyTasks) {
          const author = executors.find((user) => user.email === auth.user?.email);
          params.authorId = author.id;
        }

        if (formData.taskStatusId) {
          params.taskStatus = formData.taskStatusId;
        }

        if (formData.executorId) {
          params.executorId = formData.executorId;
        }

        if (formData.labelId) {
          params.labels = formData.labelId;
        }

        const { data: response } = await axios
          .get(routes.apiTasks(), { params, headers: auth.getAuthHeader() });

        handler(response);
      } catch (e) {
        setSubmitting(false);
        handleError(e, notify, history, auth);
      }
    },
    validateOnBlur: false,
    validateOnChange: false,
  });

  if (!executors || !labels || !taskStatuses) {
    return null;
  }

  return (
    <Card bg="light">
      <Card.Body>
        <Form onSubmit={f.handleSubmit}>
          <Row className="g-2">
            <Col md>
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
                  {taskStatuses.map((status) => (
                    <option key={status.id} value={status.id}>
                      {status.name}
                    </option>
                  ))}
                </Form.Select>
              </Form.Group>
            </Col>
            <Col md>
              <Form.Group className="mb-3">
                <Form.Label htmlFor="executorId">{t('executor')}</Form.Label>
                <Form.Select
                  nullable
                  value={f.values.executorId}
                  disabled={f.isSubmitting}
                  onChange={f.handleChange}
                  onBlur={f.handleBlur}
                  isInvalid={f.errors.executorId && f.touched.executorId}
                  id="executorId"
                  name="executorId"
                >
                  <option value="">{null}</option>
                  {executors.map((executor) => (
                    <option key={executor.id} value={executor.id}>
                      {`${executor.firstName} ${executor.lastName}`}
                    </option>
                  ))}
                </Form.Select>
              </Form.Group>
            </Col>
            <Col md>
              <Form.Group className="mb-3">
                <Form.Label htmlFor="labelId">{t('label')}</Form.Label>
                <Form.Select
                  nullable
                  value={f.values.labelId}
                  disabled={f.isSubmitting}
                  onChange={f.handleChange}
                  onBlur={f.handleBlur}
                  isInvalid={f.errors.labelId && f.touched.labelId}
                  id="labelId"
                  name="labelId"
                >
                  <option value="">{null}</option>
                  {labels.map((label) => (
                    <option key={label.id} value={label.id}>{label.name}</option>
                  ))}
                </Form.Select>
              </Form.Group>
            </Col>
          </Row>
          <Form.Group className="mb-3">
            <Form.Check
              type="checkbox"
              label={t('isMyTasks')}
              onChange={f.handleChange}
              value={f.values.isMyTasks}
            />
          </Form.Group>

          <Button variant="primary" type="submit">
            {t('show')}
          </Button>
        </Form>
      </Card.Body>
    </Card>
  );
};

export default TaskFilter;
