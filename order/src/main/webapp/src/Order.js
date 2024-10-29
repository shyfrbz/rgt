// OrderPage.js
import './App.css';
import React, { useState } from 'react';

function Order() {
    const [orderNum] = useState(1);
    const [food, setFood] = useState('');
    const [quantity, setQuantity] = useState(1);
    const [seatNum, setSeatNum] = useState(1);
    const [orderTime] = useState('');
    const [message, setMessage] = useState('');
    const [status] = useState('접수완료');

    // 주문 제출 함수
    const orderSubmit = async () => {
        const orderData = {
            orderNum,
            food,
            quantity,
            seatNum,
            orderTime,
            status
        };

        try {
            const response = await fetch('http://localhost:8080/order', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(orderData),
            });

            if (response.ok) {
                setMessage('주문이 접수되었습니다.');
            } else {
                const errorText = await response.text();
                setMessage(`주문 실패: ${errorText}`);
            }
        } catch (error) {
            setMessage(`오류 발생: ${error.message}`);
        }
    };

    return (
        <div className="App">
            <h1>음식 주문</h1>
            <table>
                <tbody>
                    <tr>
                        <td>
                            <label htmlFor="food">메뉴명</label>
                        </td>
                        <td>
                            <input type="text" name="food" id="food" value={food}
                                onChange={(e) => setFood(e.target.value)} />
                        </td>
                    </tr>
                    <tr>
                        <td>
                            <label htmlFor="quantity">수량</label>
                        </td>
                        <td>
                            <input type="number" name="quantity" id="quantity" min="1" value={quantity}
                                onChange={(e) => setQuantity(Number(e.target.value))} />
                        </td>
                    </tr>
                    <tr>
                        <td>
                            <label htmlFor="seatNum">좌석번호</label>
                        </td>
                        <td>
                            <input type="number" name="seatNum" id="seatNum" min="1" value={seatNum}
                                onChange={(e) => setSeatNum(Number(e.target.value))} />
                        </td>
                    </tr>
                </tbody>
            </table>
            <button id="submitBtn" onClick={orderSubmit}>주문하기</button>
            {message && <p>{message}</p>}
        </div>
    );
}

export default Order;
