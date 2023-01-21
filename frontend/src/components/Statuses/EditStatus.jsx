// @ts-check

import React, { useEffect, useState } from 'react';
import { useDispatch } from 'react-redux';
import { useTranslation } from 'react-i18next';
import { Form, Button } from 'react-bootstrap';
import { useFormik } from 'formik';
import * as yup from 'yup';
import { useParams, useHistory } from 'react-router-dom';
import axios from 'axios';

import { actions as taskStatusesActions } from '../../slices/taskStatusesSlice.js';
import routes from '../../routes.js';
import { useAuth, useNotify } from '../../hooks/index.js';
import handleError from '../../utils.js';

import getLogger from '../../lib/logger.js';

const log = getLogger('client');

const getValidationSchema = () => yup.object().shape({});

const EditStatus = () => {
  const { t } = useTranslation();
  const history = useHistory();
  const params = useParams();
  const auth = useAuth();
  const notify = useNotify();
  const [taskStatus, setTaskStatus] = useState(null);
  const dispatch = useDispatch();

  useEffect(() => {
    const fetchData = async () => {
      try {
        const { data } = await axios.get(routes.apiStatus(params.taskStatusId),
          { headers: auth.getAuthHeader() });
        setTaskStatus(data);
      } catch (e) {
        handleError(e, notify, history, auth);
      }
    };
    fetchData();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const f = useFormik({
    enableReinitialize: true,
    initialValues: {
      name: taskStatus ? taskStatus.name : '',
    },
    validationSchema: getValidationSchema(),
    onSubmit: async ({ name }, { setSubmitting, setErrors }) => {
      const newStatus = { name };
      try {
        const { data } = await axios.put(routes.apiStatus(params.taskStatusId),
          newStatus, { headers: auth.getAuthHeader() });
        log('status.edit', newStatus);

        dispatch(taskStatusesActions.updateTaskStatus(data));
        const from = { pathname: routes.statusesPagePath() };
        history.push(from, { message: 'statusEdited' });
      } catch (e) {
        log('label.edit.error', e);
        setSubmitting(false);
        if (e.response?.status === 422 && Array.isArray(e.response?.data)) {
          const errors = e.response.data
            .reduce((acc, err) => ({ ...acc, [err.field]: err.defaultMessage }), {});
          setErrors(errors);
          notify.addError('taskStatusEditFail');
        } else {
          handleError(e, notify, history);
        }
      }
    },
    validateOnBlur: false,
    validateOnChange: false,
  });

  if (!taskStatus) {
    return null;
  }

  return (
    <>
      <h1 className="my-4">{t('statusEdit')}</h1>
      <Form onSubmit={f.handleSubmit}>
        <Form.Group className="mb-3">
          <Form.Label htmlFor="name">{t('naming')}</Form.Label>
          <Form.Control
            className="mb-2"
            disabled={f.isSubmitting}
            onChange={f.handleChange}
            onBlur={f.handleBlur}
            value={f.values.name}
            isInvalid={f.errors.name && f.touched.name}
            name="name"
            id="name"
            type="text"
          />
          <Form.Control.Feedback type="invalid">
            {t(f.errors.name)}
          </Form.Control.Feedback>
        </Form.Group>
        <Button variant="primary" type="submit" disabled={f.isSubmitting}>
          {t('edit')}
        </Button>
      </Form>
    </>
  );
};

export default EditStatus;
