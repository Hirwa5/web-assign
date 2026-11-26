import React, { useEffect, useState } from 'react'
import axios from 'axios'
import LibraryButton from './LibraryButton'

function OfficeList() {

    const [offices, setOffices] = useState([]);
    const [search, setSearch] = useState("");

    // load all offices when the page opens
    useEffect(() => {
        axios.get("/api/office/all")
            .then(response => setOffices(response.data))
            .catch(error => alert("This is the error: " + error.message));
    }, []);

    // SEARCH by office name or office number
    const filteredOffices = offices.filter(o =>
        o.name.toLowerCase().includes(search.toLowerCase()) ||
        String(o.officeNumber).includes(search)
    );

    // DELETE
    async function deleteOffice(office) {
        if (!window.confirm("Do you want to delete office " + office.officeNumber + "?")) {
            return;
        }
        try {
            const response = await axios.delete("/api/office/delete/" + office.id);

            if (response.status == 200) {
                alert("Office deleted successfully");
                setOffices(offices.filter(o => o.id !== office.id));
            } else {
                alert("Office is not deleted");
            }
        } catch (error) {
            if (error.response && error.response.status == 500) {
                alert("Office is not deleted. It is assigned to a doctor.");
            } else {
                alert("This is the error: " + error.message + (error.response ? " - " + JSON.stringify(error.response.data) : ""));
            }
        }
    }

    return (
        <div>
            <h1>ALL OFFICES</h1>
            <label>SEARCH : </label>
            <input type="text" placeholder="Office name or number" onChange={e => setSearch(e.target.value)} /><br /><br />

            <table border="1" cellPadding="8" style={{ margin: "0 auto", borderCollapse: "collapse" }}>
                <thead>
                    <tr>
                        <th>NO</th>
                        <th>OFFICE NAME</th>
                        <th>OFFICE NUMBER</th>
                        <th>ACTION</th>
                    </tr>
                </thead>
                <tbody>
                    {filteredOffices.map((office, index) => (
                        <tr key={office.id}>
                            <td>{index + 1}</td>
                            <td>{office.name}</td>
                            <td>{office.officeNumber}</td>
                            <td>
                                <LibraryButton buttonLabel="DELETE" buttonAction={() => deleteOffice(office)} />
                            </td>
                        </tr>
                    ))}
                </tbody>
            </table>

            {filteredOffices.length == 0 && <p>No office found</p>}
        </div>
    )
}

export default OfficeList
