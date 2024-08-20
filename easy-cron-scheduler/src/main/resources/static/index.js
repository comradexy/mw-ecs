document.addEventListener('DOMContentLoaded', function () {
    var loader = document.querySelector('.loader');
    var tbody = document.querySelector('#threadPoolList tbody');

    var autoRefreshInterval;
    var autoRefreshBtn = document.getElementById('autoRefreshBtn');
    var stopAutoRefreshBtn = document.getElementById('stopAutoRefreshBtn');

    autoRefreshBtn.addEventListener('click', function () {
        if (!autoRefreshInterval) {
            autoRefreshInterval = setInterval(fetchTaskList, 3000);
            autoRefreshBtn.style.display = 'none';
            stopAutoRefreshBtn.style.display = 'inline';
        }
    });

    stopAutoRefreshBtn.addEventListener('click', function () {
        if (autoRefreshInterval) {
            clearInterval(autoRefreshInterval);
            autoRefreshInterval = null;
            autoRefreshBtn.style.display = 'inline';
            stopAutoRefreshBtn.style.display = 'none';
        }
    });

    function fetchTaskList() {
        loader.style.display = 'block';
        var xhr = new XMLHttpRequest();
        xhr.open('GET', '/schedule/api/list', true);
        xhr.onload = function () {
            if (xhr.status >= 200 && xhr.status < 300) {
                var response = JSON.parse(xhr.responseText);
                if (response.code === 200 && Array.isArray(response.data)) {
                    var html = '';
                    response.data.forEach(function (item) {
                        html += '<tr>';
                        html += '<td>' + item.key + '</td>';
                        html += '<td>' + item.desc + '</td>';
                        html += '<td>' + item.cronExpr + '</td>';
                        html += '<td><button onclick="openHandlerInfo(\'' + item.taskHandlerKey + '\')">Detail</button></td>';
                        html += '<td>' + item.initTime + '</td>';
                        html += '<td>' + item.endTime + '</td>';
                        html += '<td>' + item.execCount + '</td>';
                        html += '<td>' + item.lastExecTime + '</td>';
                        html += '<td>' + item.maxExecCount + '</td>';
                        html += '<td>' + item.state + '</td>';
                        html += '<td>' + generateOperationButton(item.state, item.key) + '</td>';
                        html += '</tr>';
                    });
                    tbody.innerHTML = html;
                } else {
                    console.error('The request was successful but the data format is incorrect!');
                }
            } else {
                console.error('The request failed!');
            }
            loader.style.display = 'none';
        };
        xhr.onerror = function () {
            console.error('The request failed!');
            loader.style.display = 'none';
        };
        xhr.send();
    }

    fetchTaskList();
});

function generateOperationButton(state, taskKey) {
    var buttonHTML = '';
    switch (state) {
        case 'Initialized':
            buttonHTML = '<button onclick="handleOperation(\'Put\', \'/schedule/api/schedule\', \'' + taskKey + '\')">Schedule</button>';
            break;
        case 'Paused':
            buttonHTML = '<button onclick="handleOperation(\'Put\', \'/schedule/api/resume\', \'' + taskKey + '\')">Resume</button>';
            break;
        case 'Running':
            buttonHTML = '<button onclick="handleOperation(\'Put\', \'/schedule/api/pause\', \'' + taskKey + '\')">Pause</button>';
            break;
        default:
            buttonHTML = '';
            break;
    }
    return buttonHTML;
}

function handleOperation(method, url, taskKey) {
    var xhr = new XMLHttpRequest();
    xhr.open(method, url, true);
    xhr.setRequestHeader('Content-Type', 'application/json');
    xhr.onload = function () {
        if (xhr.status >= 200 && xhr.status < 300) {
            console.log('Operation successful for task:', taskKey);
            fetchTaskList(); // 更新列表
        } else {
            console.error('Operation failed for task:', taskKey);
        }
    };
    xhr.onerror = function () {
        console.error('The operation request failed for task:', taskKey);
    };
    xhr.send(JSON.stringify({ taskKey: taskKey }));
}



var modal = document.getElementById("myModal");
var span = document.getElementsByClassName("close")[0];

span.onclick = function () {
    modal.style.display = "none";
}

window.onclick = function (event) {
    if (event.target == modal) {
        modal.style.display = "none";
    }
}

function openHandlerInfo(handlerKey) {
    var xhr = new XMLHttpRequest();
    xhr.open('GET', '/schedule/api/query_handler?handlerKey=' + handlerKey, true);
    xhr.onload = function () {
        if (xhr.status >= 200 && xhr.status < 300) {
            var response = JSON.parse(xhr.responseText);
            if (response.code === 200 && response.data) {
                document.getElementById('desc').value = response.data.desc;
                document.getElementById('beanClassName').value = response.data.beanClassName;
                document.getElementById('beanName').value = response.data.beanName;
                document.getElementById('methodName').value = response.data.methodName;
                modal.style.display = "block";
            } else {
                console.error('The request was successful but the data format is incorrect!');
            }
        } else {
            console.error('The request failed!');
        }
    };
    xhr.onerror = function () {
        console.error('The request failed!');
    };
    xhr.send();
}
