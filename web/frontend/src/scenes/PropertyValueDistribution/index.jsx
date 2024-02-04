import SimpleBarChart from "../../components/SimpleBarChart";
import React, { useState, useEffect } from "react";
import { BACKEND_URL } from "../../AppConfig";
import { useSessionStorage } from "@uidotdev/usehooks";
import { dateProperties } from "../../components/Filter";

const PropertyValueDistribution = (payload) => {
  const [filter, setFilter] = useSessionStorage("filterString", "");

  const [data, setData] = useState([]);
  // GET with fetch API
  useEffect(() => {
    console.log("updating bar chart");
    var myHeaders = new Headers();
    myHeaders.append("Content-Type", "application/json");

    const fetchPost = async () => {
      try {
        var raw = JSON.stringify({
          property: payload["property"],
          filter: filter,
        });

        var requestOptions = {
          method: "POST",
          headers: myHeaders,
          body: raw,
          redirect: "follow",
        };

        const response = await fetch(
          BACKEND_URL +
            "/propertyvalues?" +
            new URLSearchParams({
              property: payload["property"],
              filter: filter,
            }),
          requestOptions
        );
        const data = await response.json();
        if (data.length > 0) {
          var sum = data.reduce(function (a, b, idx) {
            if (idx > 10) {
              return a + parseInt(b.count);
            } else {
              return 0;
            }
          });
          if (data.length > 10) {
            data.length = 10;
            data.push({ count: sum, value: ".etc" });
          }
          data.reverse();
          setData(data);
        } else {
          setData(data);
        }
      } catch (error) {
        console.log(error);
      }
    };
    fetchPost();
  }, [filter]);

  let filterClick = (property, event) => {
    if (event.indexValue == ".etc") {
      return;
    }
    let newCondition = null;

    if (event.indexValue != "CONFLICT" && dateProperties.includes(property)) {
      newCondition = `${property} >= "${event.indexValue}-01-01" && ${property} <= "${event.indexValue}-12-31"`;
    } else {
      newCondition = `${property} == "${event.indexValue}"`;
    }
    console.log("new condition: [" + newCondition + "]");
    console.log("Filter: [" + filter + "]");
    if (!filter.includes(newCondition)) {
      if (filter) {
        setFilter(filter + " && " + newCondition);
      } else {
        setFilter(newCondition);
      }
    }
  };

  return (
    <SimpleBarChart
      data={data}
      property={payload["property"]}
      filterClick={filterClick}
    />
  );
};

export default PropertyValueDistribution;
