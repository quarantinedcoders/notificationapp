import React, { Component } from 'react';
import { getAllTopics, getUserCreatedTopics, getUserSubscribedTopics } from '../util/APIUtils';
import Topic from './Topic';
import { createSubscribe } from '../util/APIUtils';
import LoadingIndicator  from '../common/LoadingIndicator';
import { Button, Icon, notification } from 'antd';
import { TOPIC_LIST_SIZE } from '../constants';
import { withRouter } from 'react-router-dom';
import './TopicList.css';

class TopicList extends Component {
    constructor(props) {
        super(props);
        this.state = {
            topics: [],
            page: 0,
            size: 10,
            totalElements: 0,
            totalPages: 0,
            last: true,
            currentSubscribes: [],
            isLoading: false
        };
        this.loadTopicList = this.loadTopicList.bind(this);
        this.handleLoadMore = this.handleLoadMore.bind(this);
    }

    loadTopicList(page = 0, size = TOPIC_LIST_SIZE) {
        let promise;
        if(this.props.username) {
            if(this.props.type === 'USER_CREATED_POLLS') {
                promise = getUserCreatedTopics(this.props.username, page, size);
            } else if (this.props.type === 'USER_VOTED_POLLS') {
                promise = getUserSubscribedTopics(this.props.username, page, size);
            }
        } else {
            promise = getAllTopics(page, size);
        }

        if(!promise) {
            return;
        }

        this.setState({
            isLoading: true
        });

        promise
        .then(response => {
            const topics = this.state.topics.slice();
            const currentSubscribes = this.state.currentSubscribes.slice();

            this.setState({
                topics: topics.concat(response.content),
                page: response.page,
                size: response.size,
                totalElements: response.totalElements,
                totalPages: response.totalPages,
                last: response.last,
                currentSubscribes: currentSubscribes.concat(Array(response.content.length).fill(null)),
                isLoading: false
            })
        }).catch(error => {
            this.setState({
                isLoading: false
            })
        });

    }

    componentDidMount() {
        this.loadTopicList();
    }

    componentDidUpdate(nextProps) {
        if(this.props.isAuthenticated !== nextProps.isAuthenticated) {
            // Reset State
            this.setState({
                topics: [],
                page: 0,
                size: 10,
                totalElements: 0,
                totalPages: 0,
                last: true,
                currentSubscribes: [],
                isLoading: false
            });
            this.loadTopicList();
        }
    }

    handleLoadMore() {
        this.loadTopicList(this.state.page + 1);
    }

    handleSubscribeChange(event, topicIndex) {
        const currentSubscribes = this.state.currentSubscribes.slice();
        currentSubscribes[topicIndex] = event.target.value;

        this.setState({
            currentSubscribes: currentSubscribes
        });
    }


    handleSubscribeSubmit(event, topicIndex) {
        event.preventDefault();
        if(!this.props.isAuthenticated) {
            this.props.history.push("/login");
            notification.info({
                message: 'Notification App',
                description: "Please login to subscribe.",
            });
            return;
        }

        const topic = this.state.topics[topicIndex];
        const selectedChannel = this.state.currentSubscribes[topicIndex];

        const subscribeData = {
            topicId: topic.id,
            channelId: selectedChannel
        };

        createSubscribe(subscribeData)
        .then(response => {
            const topics = this.state.topics.slice();
            topics[topicIndex] = response;
            this.setState({
                topics: topics
            });
        }).catch(error => {
            if(error.status === 401) {
                this.props.handleLogout('/login', 'error', 'You have been logged out. Please login to subscribe');
            } else {
                notification.error({
                    message: 'Notification App',
                    description: error.message || 'Sorry! Something went wrong. Please try again!'
                });
            }
        });
    }

    render() {
        const topicViews = [];
        this.state.topics.forEach((topic, topicIndex) => {
            topicViews.push(<Topic
                key={topic.id}
                topic={topic}
                currentSubscribe={this.state.currentSubscribes[topicIndex]}
                handleSubscribeChange={(event) => this.handleSubscribeChange(event, topicIndex)}
                handleSubscribeSubmit={(event) => this.handleSubscribeSubmit(event, topicIndex)} />)
        });

        return (
            <div className="topics-container">
                {topicViews}
                {
                    !this.state.isLoading && this.state.topics.length === 0 ? (
                        <div className="no-topics-found">
                            <span>No Topics Found.</span>
                        </div>
                    ): null
                }
                {
                    !this.state.isLoading && !this.state.last ? (
                        <div className="load-more-topics">
                            <Button type="dashed" onClick={this.handleLoadMore} disabled={this.state.isLoading}>
                                <Icon type="plus" /> Load more
                            </Button>
                        </div>): null
                }
                {
                    this.state.isLoading ?
                    <LoadingIndicator />: null
                }
            </div>
        );
    }
}

export default withRouter(TopicList);
