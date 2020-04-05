import React, { Component } from 'react';
import './Topic.css';
import { Avatar, Icon } from 'antd';
import { Link } from 'react-router-dom';
import { getAvatarColor } from '../util/Colors';
import { formatDateTime } from '../util/Helpers';

import { Radio, Button } from 'antd';
const RadioGroup = Radio.Group;

class Topic extends Component {
    calculatePercentage = (channel) => {
        if(this.props.topic.totalSubscribes === 0) {
            return 0;
        }
        return (channel.subscribeCount*100)/(this.props.topic.totalSubscribes);
    };

    isSelected = (channel) => {
        return this.props.topic.selectedChannel === channel.id;
    }

    getWinningChannel = () => {
        return this.props.topic.channels.reduce((prevChannel, currentChannel) =>
            currentChannel.subscribeCount > prevChannel.subscribeCount ? currentChannel : prevChannel,
            {subscribeCount: -Infinity}
        );
    }

    getTimeRemaining = (topic) => {
        const expirationTime = new Date(topic.expirationDateTime).getTime();
        const currentTime = new Date().getTime();

        var difference_ms = expirationTime - currentTime;
        var seconds = Math.floor( (difference_ms/1000) % 60 );
        var minutes = Math.floor( (difference_ms/1000/60) % 60 );
        var hours = Math.floor( (difference_ms/(1000*60*60)) % 24 );
        var days = Math.floor( difference_ms/(1000*60*60*24) );

        let timeRemaining;

        if(days > 0) {
            timeRemaining = days + " days left";
        } else if (hours > 0) {
            timeRemaining = hours + " hours left";
        } else if (minutes > 0) {
            timeRemaining = minutes + " minutes left";
        } else if(seconds > 0) {
            timeRemaining = seconds + " seconds left";
        } else {
            timeRemaining = "less than a second left";
        }

        return timeRemaining;
    }

    render() {
        const topicChannels = [];
        if(this.props.topic.selectedChannel || this.props.topic.expired) {
            const winningChannel = this.props.topic.expired ? this.getWinningChannel() : null;

            this.props.topic.channels.forEach(channel => {
                topicChannels.push(<CompletedOrSubscribedTopicChannel
                    key={channel.id}
                    channel={channel}
                    isWinner={winningChannel && channel.id === winningChannel.id}
                    isSelected={this.isSelected(channel)}
                    percentSubscribe={this.calculatePercentage(channel)}
                />);
            });
        } else {
            this.props.topic.channels.forEach(channel => {
                topicChannels.push(<Radio className="topic-channel-radio" key={channel.id} value={channel.id}>{channel.name}</Radio>)
            })
        }
        return (
            <div className="topic-content">
                <div className="topic-header">
                    <div className="topic-creator-info">
                        <Link className="creator-link" to={`/users/${this.props.topic.createdBy.username}`}>
                            <Avatar className="topic-creator-avatar"
                                style={{ backgroundColor: getAvatarColor(this.props.topic.createdBy.name)}} >
                                {this.props.topic.createdBy.name[0].toUpperCase()}
                            </Avatar>
                            <span className="topic-creator-name">
                                {this.props.topic.createdBy.name}
                            </span>
                            <span className="topic-creator-username">
                                @{this.props.topic.createdBy.username}
                            </span>
                            <span className="topic-creation-date">
                                {formatDateTime(this.props.topic.creationDateTime)}
                            </span>
                        </Link>
                    </div>
                    <div className="topic-name">
                        {this.props.topic.name}
                    </div>
                </div>
                <div className="topic-channels">
                    <RadioGroup
                        className="topic-channel-radio-group"
                        onChange={this.props.handleSubscribeChange}
                        value={this.props.currentSubscribe}>
                        { topicChannels }
                    </RadioGroup>
                </div>
                <div className="topic-footer">
                    {
                        !(this.props.topic.selectedChannel || this.props.topic.expired) ?
                        (<Button className="subscribe-button" disabled={!this.props.currentSubscribe} onClick={this.props.handleSubscribeSubmit}>Subscribe</Button>) : null
                    }
                    <span className="total-subscribes">{this.props.topic.totalSubscribes} subscribes</span>
                    <span className="separator">•</span>
                    <span className="time-left">
                        {
                            this.props.topic.expired ? "Final results" :
                            this.getTimeRemaining(this.props.topic)
                        }
                    </span>
                </div>
            </div>
        );
    }
}

function CompletedOrSubscribedTopicChannel(props) {
    return (
        <div className="cv-topic-channel">
            <span className="cv-topic-channel-details">
                <span className="cv-channel-percentage">
                    {Math.round(props.percentSubscribe * 100) / 100}%
                </span>
                <span className="cv-channel-text">
                    {props.channel.text}
                </span>
                {
                    props.isSelected ? (
                    <Icon
                        className="selected-channel-icon"
                        type="check-circle-o"
                    /> ): null
                }
            </span>
            <span className={props.isWinner ? 'cv-channel-percent-chart winner': 'cv-channel-percent-chart'}
                style={{width: props.percentSubscribe + '%' }}>
            </span>
        </div>
    );
}


export default Topic;
