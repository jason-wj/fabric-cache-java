#!/usr/bin/env bash

MODE=$1
STATE=$2

# 项目名(根据项目改，同时需要改掉docker-compose.yml中的flag)
PROJECT_NAME=fabric

# 项目根路径
ROOT_PATH=$(pwd)

# docker和项目文件映射地址
RUN_PATH=${ROOT_PATH}


# 外部触发指令
# 用户级
COMMAND_MONGO="mongo"
COMMAND_MYSQL="mysql"
COMMAND_REDIS="redis"

# container
IMAGE_CONTAINER_MONGO=${PROJECT_NAME}"-"${COMMAND_MONGO}
IMAGE_CONTAINER_MYSQL=${PROJECT_NAME}"-"${COMMAND_MYSQL}
IMAGE_CONTAINER_REDIS=${PROJECT_NAME}"-"${COMMAND_REDIS}


# 启动项目
function start_state() {
    # 登录
    if [[ ${STATE} == "" ]]; then
        printHelp
        exit 1
    elif [[ ${STATE} == "all" ]]; then
        start_all
    else
        start_one ${STATE}
    fi
}

# 日志查看
function logs_state() {
        case ${STATE} in
        ${COMMAND_MYSQL})
            docker logs -f ${IMAGE_CONTAINER_MYSQL} --tail 10
        ;;
        ${COMMAND_MONGO})
            docker logs -f ${IMAGE_CONTAINER_MONGO} --tail 10
        ;;
        ${COMMAND_REDIS})
            docker logs -f ${IMAGE_CONTAINER_REDIS} --tail 10
        ;;
        *)
            docker logs -f $1 --tail 10
    esac
}

# 启动所有项目
function start() {
    if [[ ${STATE} == "" ]]; then
        printHelp
        exit 1
    elif [[ ${STATE} == "all" ]]; then
        start_all
    else
        start_one "${STATE}"
    fi
}

# 启动指定服务
function start_one() {
    # 程序配置文件的正常读取是在该目录下进行的
    case $1 in
        ${COMMAND_MYSQL})
            IMAGE_CONTAINER_MYSQL=${IMAGE_CONTAINER_MYSQL} RUN_PATH=${RUN_PATH} docker-compose -f "${ROOT_PATH}"/docker-compose.yml up -d ${IMAGE_CONTAINER_MYSQL}
        ;;
        ${COMMAND_MONGO})
            IMAGE_CONTAINER_MONGO=${IMAGE_CONTAINER_MONGO} RUN_PATH=${RUN_PATH} docker-compose -f "${ROOT_PATH}"/docker-compose.yml up -d ${IMAGE_CONTAINER_MONGO}
        ;;
        ${COMMAND_REDIS})
            IMAGE_CONTAINER_REDIS=${IMAGE_CONTAINER_REDIS} RUN_PATH=${RUN_PATH} docker-compose -f "${ROOT_PATH}"/docker-compose.yml up -d ${IMAGE_CONTAINER_REDIS}
        ;;
    esac
}

function release_state() {
    if [[ ${STATE} == "" ]]; then
        printHelp
        exit 1
    elif [[ ${STATE} == "all" ]]; then
        release_all
    else
        release_one "${STATE}"
    fi
}

# 全局释放所有docker环境，当前系统所有镜像都会受到影响
# 不确定则请慎用
function release_all() {
    # 关闭当前系统正在运行的容器，并清除
    docker stop $(docker ps -a | awk '{ print $1}' | tail -n +2)
    docker rm -f $(docker ps -a | awk '{ print $1}' | tail -n +2)

    docker volume ls -qf dangling=true
    # 查看指定的volume
    # docker inspect docker_orderer.example.com

    # 开始清理
    docker volume rm $(docker volume ls -qf dangling=true)
    # 删除为none的镜像
    docker images | grep none | awk '{print $3}' | xargs docker rmi -f
    docker images --no-trunc | grep '<none>' | awk '{ print $3 }' | xargs docker rmi

    # 该指令默认会清除所有如下资源：
    #
    # 已停止的容器（container）
    # 未被任何容器所使用的卷（volume）
    # 未被任何容器所关联的网络（network）
    # 所有悬空镜像（image）。
    #该指令默认只会清除悬空镜像，未被使用的镜像不会被删除。添加-a 或 --all参数后，可以一并清除所有未使用的镜像和悬空镜像。
    docker system prune -f

    # 删除无用的卷
    docker volume prune -f

    # 删除无用网络
    docker network prune -f
}

# 清理关闭一个指定容器
function release_one() {
    case $1 in
        ${COMMAND_MYSQL})
            RUN_PATH=${ROOT_PATH} docker-compose stop ${IMAGE_CONTAINER_MYSQL}
            docker-compose rm -f ${IMAGE_CONTAINER_MYSQL}
        ;;
        ${COMMAND_MONGO})
            RUN_PATH=${ROOT_PATH} docker-compose stop ${IMAGE_CONTAINER_MONGO}
            docker-compose rm -f ${IMAGE_CONTAINER_MONGO}
        ;;
        ${COMMAND_REDIS})
            RUN_PATH=${ROOT_PATH} docker-compose stop ${IMAGE_CONTAINER_REDIS}
            docker-compose rm -f ${IMAGE_CONTAINER_REDIS}
        ;;
        *)
            RUN_PATH=${ROOT_PATH} docker-compose stop $1
            docker-compose rm -f $1
            docker rmi -f $1
            exit 1
    esac
}

function printHelp() {
    echo "./main.sh start [+操作码]：启动服务"
    echo "          [操作码]"
    echo "               all：启动所有服务"
    echo "               指定服务：启动指定服务,当前支持[mysql,redis]"
    echo "./main.sh logs [+操作码]：查看日志"
    echo "          [操作码]"
    echo "               指定服务：查看指定日志,当前支持[mysql,redis]"
    echo "./main.sh release [+操作码]：用于释放项目和其余容器"
    echo "          [操作码]"
    echo "               all：释放项目所有内容，包括各种容器"
    echo "               指定容器名：释放指定容器，主要是用来释放项目所在的容器,当前支持[mysql,redis]"
    echo "其余操作将触发此说明"
}

#启动模式
case ${MODE} in
    "start")
        start_state ;;
    "logs")
        logs_state ;;
    "release")
        release_state ;;
    *)
        printHelp
        exit 1
esac