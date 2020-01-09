<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
<?php

$db_host = "localhost";
$db_user = "root";
$db_passwd = "apmsetup";
$db_name = "drink";


// MySQL - DB 접속.
$conn = mysqli_connect($db_host,$db_user,$db_passwd,$db_name);

if (mysqli_connect_errno()){

    echo "MySQL 연결 오류: " . mysqli_connect_error();

    exit;

} else {

    echo "DB : \"$db_name\" 연결됨.<br/>";
    $id=$_GET["id"];
    $alcohol=$_GET["alcohol"];

    echo "id:";
    echo $id;
    echo "/alcohol:";
    echo $alcohol;
 

    $sql = "INSERT INTO photo VALUES($id, CURRENT_TIMESTAMP, 'C:\APMSETUP', $alcohol)";
    $result = mysqli_query($conn, $sql);
    mysqli_close($conn);
}
?>
