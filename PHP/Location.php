
<?php

$con = mysqli_connect("localhost", "root","apmsetup" ,"honeysleep");

if(mysqli_connect_errno($con)){
	echo "Fail connect mysql".mysqli_connect_error();
}

mysqli_set_charset($con, "utf8");


$res = mysqli_query($con, "select * from test3 ORDER BY date ASC limit 1");

$result = array();

while($row = mysqli_fetch_array($res)){
	array_push($result, 
		array('lati'=>$row[3], 'longi'=>$row[4], 'phone_number'=>$row[6]));
}

echo json_encode(array("result"=>$result));


mysqli_close($con);
?>