import React, { Component } from 'react';
import { createTopic } from '../util/APIUtils';
import { MAX_CHOICES, TOPIC_NAME_MAX_LENGTH, TOPIC_CHANNEL_MAX_LENGTH } from '../constants';
import './NewTopic.css';
import { Form, Input, Button, Icon, Select, Col, notification } from 'antd';
const Option = Select.Option;
const FormItem = Form.Item;
const { TextArea } = Input

class NewTopic extends Component {
    constructor(props) {
        super(props);
        this.state = {
            name: {
                text: ''
            },
            channels: [{
                text: ''
            }, {
                text: ''
            }],
            topicLength: {
                days: 1,
                hours: 0
            }
        };
        this.addChannel = this.addChannel.bind(this);
        this.removeChannel = this.removeChannel.bind(this);
        this.handleSubmit = this.handleSubmit.bind(this);
        this.handleQuestionChange = this.handleQuestionChange.bind(this);
        this.handleChannelChange = this.handleChannelChange.bind(this);
        this.handleTopicDaysChange = this.handleTopicDaysChange.bind(this);
        this.handleTopicHoursChange = this.handleTopicHoursChange.bind(this);
        this.isFormInvalid = this.isFormInvalid.bind(this);
    }

    addChannel(event) {
        const channels = this.state.channels.slice();
        this.setState({
            channels: channels.concat([{
                text: ''
            }])
        });
    }

    removeChannel(channelNumber) {
        const channels = this.state.channels.slice();
        this.setState({
            channels: [...channels.slice(0, channelNumber), ...channels.slice(channelNumber+1)]
        });
    }

    handleSubmit(event) {
        event.preventDefault();
        const topicData = {
            name: this.state.name.text,
            channels: this.state.channels.map(channel => {
                return {name: channel.text}
            }),
            topicLength: this.state.topicLength
        };

        createTopic(topicData)
        .then(response => {
            this.props.history.push("/");
        }).catch(error => {
            if(error.status === 401) {
                this.props.handleLogout('/login', 'error', 'You have been logged out. Please login create topic.');
            } else {
                notification.error({
                    message: 'Notification App',
                    description: error.message || 'Sorry! Something went wrong. Please try again!'
                });
            }
        });
    }

    validateTopicName = (nameText) => {
        if(nameText.length === 0) {
            return {
                validateStatus: 'error',
                errorMsg: 'Please enter your name!'
            }
        } else if (nameText.length > TOPIC_NAME_MAX_LENGTH) {
            return {
                validateStatus: 'error',
                errorMsg: `Question is too long (Maximum ${TOPIC_NAME_MAX_LENGTH} characters allowed)`
            }
        } else {
            return {
                validateStatus: 'success',
                errorMsg: null
            }
        }
    }

    handleQuestionChange(event) {
        const value = event.target.value;
        this.setState({
            name: {
                text: value,
                ...this.validateTopicName(value)
            }
        });
    }

    validateChannel = (channelText) => {
        if(channelText.length === 0) {
            return {
                validateStatus: 'error',
                errorMsg: 'Please enter a channel!'
            }
        } else if (channelText.length > TOPIC_CHANNEL_MAX_LENGTH) {
            return {
                validateStatus: 'error',
                errorMsg: `Channel is too long (Maximum ${TOPIC_CHANNEL_MAX_LENGTH} characters allowed)`
            }
        } else {
            return {
                validateStatus: 'success',
                errorMsg: null
            }
        }
    }

    handleChannelChange(event, index) {
        const channels = this.state.channels.slice();
        const value = event.target.value;

        channels[index] = {
            text: value,
            ...this.validateChannel(value)
        }

        this.setState({
            channels: channels
        });
    }


    handleTopicDaysChange(value) {
        const topicLength = Object.assign(this.state.topicLength, {days: value});
        this.setState({
            topicLength: topicLength
        });
    }

    handleTopicHoursChange(value) {
        const topicLength = Object.assign(this.state.topicLength, {hours: value});
        this.setState({
            topicLength: topicLength
        });
    }

    isFormInvalid() {
        if(this.state.name.validateStatus !== 'success') {
            return true;
        }

        for(let i = 0; i < this.state.channels.length; i++) {
            const channel = this.state.channels[i];
            if(channel.validateStatus !== 'success') {
                return true;
            }
        }
    }

    render() {
        const channelViews = [];
        this.state.channels.forEach((channel, index) => {
            channelViews.push(<TopicChannel key={index} channel={channel} channelNumber={index} removeChannel={this.removeChannel} handleChannelChange={this.handleChannelChange}/>);
        });

        return (
            <div className="new-topic-container">
                <h1 className="page-title">Create Topic</h1>
                <div className="new-topic-content">
                    <Form onSubmit={this.handleSubmit} className="create-topic-form">
                        <FormItem validateStatus={this.state.name.validateStatus}
                            help={this.state.name.errorMsg} className="topic-form-row">
                        <TextArea
                            placeholder="Enter your name"
                            style = {{ fontSize: '16px' }}
                            autosize={{ minRows: 3, maxRows: 6 }}
                            name = "name"
                            value = {this.state.name.text}
                            onChange = {this.handleQuestionChange} />
                        </FormItem>
                        {channelViews}
                        <FormItem className="topic-form-row">
                            <Button type="dashed" onClick={this.addChannel} disabled={this.state.channels.length === MAX_CHOICES}>
                                <Icon type="plus" /> Add a channel
                            </Button>
                        </FormItem>
                        <FormItem className="topic-form-row">
                            <Col xs={24} sm={4}>
                                Topic length:
                            </Col>
                            <Col xs={24} sm={20}>
                                <span style = {{ marginRight: '18px' }}>
                                    <Select
                                        name="days"
                                        defaultValue="1"
                                        onChange={this.handleTopicDaysChange}
                                        value={this.state.topicLength.days}
                                        style={{ width: 60 }} >
                                        {
                                            Array.from(Array(8).keys()).map(i =>
                                                <Option key={i}>{i}</Option>
                                            )
                                        }
                                    </Select> &nbsp;Days
                                </span>
                                <span>
                                    <Select
                                        name="hours"
                                        defaultValue="0"
                                        onChange={this.handleTopicHoursChange}
                                        value={this.state.topicLength.hours}
                                        style={{ width: 60 }} >
                                        {
                                            Array.from(Array(24).keys()).map(i =>
                                                <Option key={i}>{i}</Option>
                                            )
                                        }
                                    </Select> &nbsp;Hours
                                </span>
                            </Col>
                        </FormItem>
                        <FormItem className="topic-form-row">
                            <Button type="primary"
                                htmlType="submit"
                                size="large"
                                disabled={this.isFormInvalid()}
                                className="create-topic-form-button">Create Topic</Button>
                        </FormItem>
                    </Form>
                </div>
            </div>
        );
    }
}

function TopicChannel(props) {
    return (
        <FormItem validateStatus={props.channel.validateStatus}
        help={props.channel.errorMsg} className="topic-form-row">
            <Input
                placeholder = {'Channel ' + (props.channelNumber + 1)}
                size="large"
                value={props.channel.text}
                className={ props.channelNumber > 1 ? "optional-channel": null}
                onChange={(event) => props.handleChannelChange(event, props.channelNumber)} />

            {
                props.channelNumber > 1 ? (
                <Icon
                    className="dynamic-delete-button"
                    type="close"
                    disabled={props.channelNumber <= 1}
                    onClick={() => props.removeChannel(props.channelNumber)}
                /> ): null
            }
        </FormItem>
    );
}


export default NewTopic;
