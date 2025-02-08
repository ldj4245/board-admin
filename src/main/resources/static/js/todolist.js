$(document).ready(() => {
    // 할일 추가하는 함수를 별도로 분리
    const addTodoItem = () => {
        const text = $('#todo-input').val();
        if (!text) return; // 빈 텍스트는 추가하지 않음

        const todoElement = `
        <li>
          <span class="handle">
            <i class="fas fa-ellipsis-v"></i>
            <i class="fas fa-ellipsis-v"></i>
          </span>
          <div  class="icheck-primary d-inline ml-2">
            <input type="checkbox" value="" name="todo3">
            <label for="todoCheck3"></label>
          </div>
          <span class="text">${text}</span>
          <div class="tools">
            <i class="fas fa-edit"></i>
            <i class="fas fa-trash-o"></i>
          </div>
        </li>
        `;
        $('ul.todo-list').append(todoElement);

        // 입력창 비우기
        $('#todo-input').val('');
    };

    // 버튼 클릭 이벤트
    $('#todo-button').click(addTodoItem);

    // 엔터키 이벤트 (keyup 대신 keypress 사용)
    $('#todo-input').keypress((event) => {
        if(event.keyCode === 13) {
            event.preventDefault(); // 엔터키의 기본 동작 방지
            addTodoItem();
        }
    });

    // 더블클릭 이벤트
    $(document).on('dblclick','ul.todo-list li', function() {
        $(this).toggleClass('done').fadeOut('slow');
    });
});