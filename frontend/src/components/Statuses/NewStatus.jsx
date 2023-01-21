// @ts-check

import React from 'react';
import { useDispatch } from 'react-redux';
import { useTranslation } from 'react-i18next';
import { Form, Button } from 'react-bootstrap';
import { useFormik } from 'formik';
import * as yup from 'yup';
import { useHistory } from 'react-router-dom';
import axios from 'axios';

import { actions as taskStatusesActions } from '../../slices/taskStatusesSlice.js';
import routes from '../../routes.js';
import { useAuth, useNotify } from '../../hooks/index.js';

import handleError from '../../utils.js';
import getLogger from '../../lib/logger.js';

const log = getLogger('client');

const getValidationSchema = () => yup.object().shape({});

const NewStatus = () => {
  const { t } = useTranslation();
  const history = useHistory();
  const auth = useAuth();
  const notify = useNotify();
  const dispatch = useDispatch();

  const f = useFormik({
    initialValues: {
      name: '',
    },
    validationSchema: getValidationSchema(),
    onSubmit: async ({ name }, { setSubmitting, setErrors }) => {
      const status = { name };
      try {
        log('status.create', status);
        const { data } = await axios
          .post(routes.apiStatuses(), status, { headers: auth.getAuthHeader() });
        dispatch(taskStatusesActions.addTaskStatus(data));
        const from = { pathname: routes.statusesPagePath() };
        history.push(from, { message: 'statusCreated' });
      } catch (e) {
        log('label.create.error', e);
        setSubmitting(false);
        if (e.response?.status === 422 && Array.isArray(e.response?.data)) {
          const errors = e.response.data
            .reduce((acc, err) => ({ ...acc, [err.field]: err.defaultMessage }), {});
          setErrors(errors);
          notify.addError('taskStatusCreateFail');
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
      <h1 className="my-4">{t('statusCreating')}</h1>
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
          {t('create')}
        </Button>
      </Form>
    </>
  );
};

export default NewStatus;
