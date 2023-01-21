// @ts-check

import React, { useEffect, useState } from 'react';
import { useDispatch } from 'react-redux';
import { useTranslation } from 'react-i18next';
import { useParams, Link, useHistory } from 'react-router-dom';
import {
  Card, Button, Container, Row, Col, Form,
} from 'react-bootstrap';
import axios from 'axios';

import { actions as tasksActions } from '../../slices/tasksSlice.js';
import handleError from '../../utils.js';
import routes from '../../routes.js';
import { useAuth, useNotify } from '../../hooks/index.js';

import getLogger from '../../lib/logger.js';

const log = getLogger('client');
log.enabled = true;

const Task = () => {
  const { t } = useTranslation();
  const params = useParams();
  const auth = useAuth();
  const notify = useNotify();
  const history = useHistory();
  const dispatch = useDispatch();

  const [task, setTask] = useState(null);

  useEffect(() => {
    const fetchData = async () => {
      try {
        const { data: taskData } = await axios.get(routes.apiTask(params.taskId),
          { headers: auth.getAuthHeader() });
        setTask(taskData);
      } catch (e) {
        handleError(e, notify, history);
      }
    };
    fetchData();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [dispatch]);

  if (!task) {
    return null;
  }

  const removeTask = async (event, id) => {
    event.preventDefault();
    try {
      await axios.delete(routes.apiTask(id), { headers: auth.getAuthHeader() });
      dispatch(tasksActions.removeTask(id));
      const from = { pathname: routes.tasksPagePath() };
      history.push(from, { message: 'taskRemoved' });
    } catch (e) {
      handleError(e, notify, history, auth);
    }
  };

  return (
    <Card>
      <Card.Header className="bg-secondary text-white">
        <Card.Title>{task.name}</Card.Title>
      </Card.Header>
      <Card.Body>
        <p>{task.description}</p>
        <Container>
          <Row>
            <Col>
              {t('author')}
            </Col>
            <Col>
              {`${task.author.firstName} ${task.author.lastName}`}
            </Col>
          </Row>
          {task.executor && (
            <Row>
              <Col>
                {t('executor')}
              </Col>
              <Col>
                {`${task.executor.firstName} ${task.executor.lastName}`}
              </Col>
            </Row>
          )}
          <Row>
            <Col>
              {t('status')}
            </Col>
            <Col>
              {task.taskStatus.name}
            </Col>
          </Row>
          <Row>
            <Col>
              {t('createDate')}
            </Col>
            <Col>
              {new Date(task.createdAt).toLocaleString('ru')}
            </Col>
          </Row>
          <Row>
            <Col>
              {t('labels')}
              :
              <ul>
                {task.labels?.map((label) => (<li key={label.id}>{label.name}</li>))}
              </ul>
            </Col>
          </Row>
          <Row>
            <Col>
              <Link to={routes.taskEditPagePath(task.id)}>{t('edit')}</Link>
              <Form onSubmit={(e) => removeTask(e, task.id)}>
                <Button type="submit" variant="link">Удалить</Button>
              </Form>
            </Col>
          </Row>
        </Container>
      </Card.Body>
    </Card>
  );
};

export default Task;
